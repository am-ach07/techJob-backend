package com.techJob.DTOs.auth;

import com.techJob.response.JWTResponseDTO;

public class AuthResult {
    private JWTResponseDTO jwt;
    private boolean web;

    public AuthResult(JWTResponseDTO jwt, boolean web) {
        this.jwt = jwt;
        this.web = web;
    }

    public JWTResponseDTO getJwt() { return jwt; }
    public boolean isWeb() { return web; }
}
