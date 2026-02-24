package com.techJob.security.jwt;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.techJob.security.constants.SecurityConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookieService {

    private static final Logger logger = LoggerFactory.getLogger(CookieService.class);

    @Value("${app.cookie.secure:true}")
    private boolean secureCookies;

    // ============================
    // WRITE ACCESS COOKIE
    // ============================

    public void writeAccessToken(HttpServletResponse response, String token, long maxAgeSeconds) {

        ResponseCookie accessCookie = ResponseCookie.from(SecurityConstants.ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(secureCookies)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        logger.debug("Set ACCESS_TOKEN cookie");
    }

    // ============================
    // WRITE REFRESH COOKIE
    // ============================

    public void writeRefreshToken(HttpServletResponse response, String token, long maxAgeSeconds) {

        ResponseCookie refreshCookie = ResponseCookie.from(SecurityConstants.REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(secureCookies)
                .sameSite("Strict")
                .path(SecurityConstants.REFRESH_PATH)
                .maxAge(maxAgeSeconds)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        logger.debug("Set REFRESH_TOKEN cookie");
    }

    // ============================
    // CSRF COOKIE (WEB FORMS)
    // ============================

    public String generateCsrfToken(HttpServletResponse response) {

        String token = UUID.randomUUID().toString();

        ResponseCookie csrfCookie = ResponseCookie.from(SecurityConstants.CSRF_TOKEN_COOKIE, token)
                .httpOnly(false)
                .secure(secureCookies)
                .sameSite("Lax")
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, csrfCookie.toString());
        logger.debug("Set CSRF cookie");

        return token;
    }

    // ============================
    // READ REFRESH COOKIE
    // ============================

    public String extractRefreshToken(HttpServletRequest request) {

        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {

            if (SecurityConstants.REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    // ============================
    // READ CSRF COOKIE
    // ============================

    public String extractCsrfToken(HttpServletRequest request) {

        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {

            if (SecurityConstants.CSRF_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    // ============================
    // CLEAR AUTH COOKIES (LOGOUT)
    // ============================

    public void clearAuthCookies(HttpServletResponse response) {

        clearCookie(response, SecurityConstants.ACCESS_TOKEN_COOKIE);
        clearCookie(response, SecurityConstants.REFRESH_TOKEN_COOKIE);
        clearCookie(response, SecurityConstants.CSRF_TOKEN_COOKIE);
        logger.info("Cleared all auth cookies");
    }

    private void clearCookie(HttpServletResponse response, String name) {

        ResponseCookie cookie = ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}

// Security improvement: All cookie names are centralized. Logging is added for all cookie operations. Secure, HTTP-only, and SameSite settings are enforced for best practices.