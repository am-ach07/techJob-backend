package com.techJob.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.techJob.security.constants.SecurityConstants;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookieService {

    private static final Logger logger = LoggerFactory.getLogger(CookieService.class);

    @Value("${app.cookie.secure:false}")
    private boolean secureCookies;


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

   

    public void clearCookie(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from(SecurityConstants.REFRESH_TOKEN_COOKIE, "")
                .path(SecurityConstants.REFRESH_PATH)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}

