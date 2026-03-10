package com.techJob.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.techJob.domain.entity.User;
import com.techJob.repository.UserRepository;
import com.techJob.security.constants.SecurityConstants;
import com.techJob.service.CustomUserDetailsService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
	private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
		this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith(SecurityConstants.AUTH_LOGIN_API_PATH)
        		|| path.startsWith(SecurityConstants.AUTH_REFRESH_API_PATH)
        		|| path.startsWith(SecurityConstants.AUTH_REGISTER_API_PATH)
                || path.startsWith(SecurityConstants.SWAGGER_PATH)
                || path.startsWith(SecurityConstants.API_DOCS_PATH)
                || path.equals(SecurityConstants.LOGIN_PATH)
                || path.equals(SecurityConstants.REGISTER_PATH);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        // already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String accessToken = extractAccessToken(request);

        if (!StringUtils.hasText(accessToken)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // Extract publicID from JWT (subject)
            String publicID = jwtService.extractSubject(accessToken);
            
            // Load user by publicID 
            UserDetails userDetails = userDetailsService.loadUserByPublicID(publicID);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            tokenVersionIsValid(publicID,accessToken);

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Authenticated request for publicID: {}", publicID);
        } catch (JwtException ex) {
            SecurityContextHolder.clearContext();
            response.setHeader(SecurityConstants.TOKEN_EXPIRED_HEADER, "true");
            logger.warn("JWT rejected: {}", ex.getMessage());
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            logger.error("Authentication error: {}", ex.getMessage());
        }

        chain.doFilter(request, response);
    }

    private String extractAccessToken(HttpServletRequest request) {
        // Mobile (Authorization Header)
        String authHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        logger.info("Header: {}", request.getHeader(SecurityConstants.AUTHORIZATION_HEADER));
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        return null;
    }
    
    private void tokenVersionIsValid(String publicID,String token) {
    	User user = userRepository.findByPublicID(publicID)
    	        .orElseThrow();

    	Integer tokenVersionFromToken =
    	        jwtService.isTokenVersionValid(token);

    	if (!user.getTokenVersion().equals(tokenVersionFromToken)) {
    	    throw new BadCredentialsException("Token is invalid - session expired");
    	}
    }
}
