package com.DTOs.auth;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequestDTO {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    // Constructors
    public RefreshTokenRequestDTO() {}

    public RefreshTokenRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Getter & Setter
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
