package mp.gov.ftms.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mp.gov.ftms.dto.AuthResponse;
import mp.gov.ftms.dto.LoginRequest;
import mp.gov.ftms.dto.PasswordResetRequest;
import mp.gov.ftms.dto.RefreshTokenRequest;
import mp.gov.ftms.dto.UserProfile;
import mp.gov.ftms.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return authService.login(request, servletRequest.getRemoteAddr());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @GetMapping("/me")
    public UserProfile me(Authentication authentication) {
        return authService.me(authentication.getName());
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<Map<String, String>> requestReset(@Valid @RequestBody PasswordResetRequest request,
                                                            HttpServletRequest servletRequest) {
        authService.requestPasswordReset(request, servletRequest.getRemoteAddr());
        return ResponseEntity.accepted().body(Map.of("status", "RESET_FLOW_QUEUED"));
    }
}

