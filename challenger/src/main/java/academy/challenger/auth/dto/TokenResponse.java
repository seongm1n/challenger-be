package academy.challenger.auth.dto;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    long accessTokenExpiry,
    long refreshTokenExpiry,
    String tokenType
) {}
