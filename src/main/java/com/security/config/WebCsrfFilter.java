package com.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security.constants.SecurityConstants;
import com.security.jwt.CookieService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

@Component
public class WebCsrfFilter extends OncePerRequestFilter {

    private final CookieService cookieService;

    private static final Logger logger = LoggerFactory.getLogger(WebCsrfFilter.class);

    private static final Set<String> SAFE_METHODS = Set.of(
            HttpMethod.GET.name(),
            HttpMethod.HEAD.name(),
            HttpMethod.OPTIONS.name(),
            HttpMethod.TRACE.name()
    );

    public WebCsrfFilter(CookieService cookieService) {
        this.cookieService = cookieService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // API (Mobile) does not require CSRF protection
        if (path.startsWith("/api/")) {
            return true;
        }

        // Public pages
        return 
                 path.startsWith(SecurityConstants.SWAGGER_PATH)
                || path.startsWith(SecurityConstants.API_DOCS_PATH);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        // Allow safe methods
        if (SAFE_METHODS.contains(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Read token from header or form
        String csrfHeader = request.getHeader(SecurityConstants.CSRF_HEADER);
        if (csrfHeader == null || csrfHeader.isBlank()) {
            csrfHeader = request.getParameter(SecurityConstants.CSRF_PARAM); // HTML form fallback
        }

        String csrfCookie = cookieService.extractCsrfToken(request);

        // Validate CSRF token: must match cookie and header/param
        if (csrfCookie == null || csrfHeader == null || !csrfCookie.equals(csrfHeader)) {
            logger.warn("CSRF validation failed for path: {}", request.getServletPath());
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid CSRF token"
            );
            return;
        }

        chain.doFilter(request, response);
    }
}
// Security improvements:
// - Robust CSRF protection for web forms (header or _csrf param)
// - No CSRF required for mobile/API flows
// - Logging added for CSRF failures
// - All names are standardized via SecurityConstants