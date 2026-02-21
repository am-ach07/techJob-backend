package com.security.enums;

/**
 * Enum for standardizing token types (ACCESS, REFRESH, CSRF, etc.).
 * Using enums prevents typos and improves maintainability.
 */
public enum TokenType {
    ACCESS,
    REFRESH,
    CSRF
}
