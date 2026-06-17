package mp.gov.ftms.service;

import mp.gov.ftms.common.BusinessException;
import mp.gov.ftms.domain.UserAccount;
import mp.gov.ftms.dto.AuthResponse;
import mp.gov.ftms.dto.LoginRequest;
import mp.gov.ftms.dto.PasswordResetRequest;
import mp.gov.ftms.dto.RefreshTokenRequest;
import mp.gov.ftms.dto.UserProfile;
import mp.gov.ftms.repository.UserAccountRepository;
import mp.gov.ftms.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userRepository;
    private final JwtService jwtService;
    private final AuditService auditService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserAccountRepository userRepository,
                       JwtService jwtService,
                       AuditService auditService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.auditService = auditService;
    }

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {
        UserAccount user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (user.isLocked()) {
            throw new BusinessException("Account is locked. Contact State Admin.");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException ex) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLocked(true);
            }
            userRepository.save(user);
            auditService.record(request.email(), "AUTH_FAILED", "UserAccount", user.getId().toString(), ipAddress, "Failed login attempt");
            throw ex;
        }

        user.setFailedAttempts(0);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        auditService.record(user.getEmail(), "AUTH_SUCCESS", "UserAccount", user.getId().toString(), ipAddress, "JWT session issued");
        return new AuthResponse(jwtService.createAccessToken(user), jwtService.createRefreshToken(user), profile(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshTokenRequest request) {
        if (!jwtService.isTokenType(request.refreshToken(), "refresh")) {
            throw new BusinessException("Invalid refresh token");
        }
        UserAccount user = userRepository.findByEmailIgnoreCase(jwtService.subject(request.refreshToken()))
                .orElseThrow(() -> new BusinessException("User no longer exists"));
        return new AuthResponse(jwtService.createAccessToken(user), jwtService.createRefreshToken(user), profile(user));
    }

    @Transactional(readOnly = true)
    public UserProfile me(String email) {
        return userRepository.findByEmailIgnoreCase(email).map(this::profile)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    @Transactional
    public void requestPasswordReset(PasswordResetRequest request, String ipAddress) {
        userRepository.findByEmailIgnoreCase(request.email()).ifPresent(user ->
                auditService.record(user.getEmail(), "PASSWORD_RESET_REQUESTED", "UserAccount", user.getId().toString(), ipAddress,
                        "Mock reset token generated and notification queued"));
    }

    private UserProfile profile(UserAccount user) {
        return new UserProfile(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getDesignation(),
                user.getRole().getName(),
                user.getDepartment() == null ? "STATE" : user.getDepartment().getCode(),
                user.getDepartment() == null ? "State Administration" : user.getDepartment().getNameEn(),
                user.getDepartment() == null ? "राज्य प्रशासन" : user.getDepartment().getNameHi(),
                user.getRole().getPermissions()
        );
    }
}

