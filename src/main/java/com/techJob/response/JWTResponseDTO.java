package com.techJob.response;

public class JWTResponseDTO {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer"; // عادة Bearer
    private Long expiresIn; // وقت انتهاء صلاحية الـ access token بالمللي ثانية

    // Constructors
    public JWTResponseDTO() {}

    public JWTResponseDTO(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    // Getters & Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
