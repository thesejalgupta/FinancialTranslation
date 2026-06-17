package mp.gov.ftms.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserProfile user
) {
}

