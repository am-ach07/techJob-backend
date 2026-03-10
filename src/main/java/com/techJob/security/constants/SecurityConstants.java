package com.techJob.security.constants;

/**
 * Centralized security-related constants for cookie names, header names, and paths.
 * This reduces duplication and risk of typos across the codebase.
 */
public final class SecurityConstants {
    private SecurityConstants() {}

    // Cookie Names
    public static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN_COOKIE = "REFRESH_TOKEN";

    // Header Names
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_EXPIRED_HEADER = "X-TOKEN-EXPIRED";
    //resend limit keys
    public static final String KEY_LOGIN="LOGIN_";
    public static final String KEY_RESEND="RESEND_";
    //auth auditLog 
    public static final String USER_AGENT_HEADER="User-Agent";
    public static final String LOGIN_ACTION="LOGIN";
    public static final String LOGOUT_ACTION="LOGOUT";
    public static final String REFRESH_ACTION="REFRESH";
    public static final String FAILED_ACTION="FAILED";
    public static final String SUCCESS_ACTION="SUCCESS";
    // Auth Paths
    public static final String AUTH_LOGIN_API_PATH = "/api/v1/auth/login";
    public static final String AUTH_REGISTER_API_PATH = "/api/v1/auth/register";
    public static final String AUTH_REFRESH_API_PATH = "/api/v1/auth/refresh";
    public static final String AUTH_API_PATH = "/api/v1/auth/";

    public static final String SWAGGER_PATH = "/swagger-ui/";
    public static final String API_DOCS_PATH = "/v3/api-docs/";
    public static final String LOGIN_PATH = "/auth/login";
    public static final String REGISTER_PATH = "/auth/register";
    public static final String REFRESH_PATH = "/api/v1/auth/refresh";
}