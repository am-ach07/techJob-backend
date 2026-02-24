package com.techJob.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.techJob.domain.entity.User;
import com.techJob.security.enums.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    // ============================
    // TOKEN GENERATION
    // ============================

    /**
     * Generate access token using publicID as subject. Username is included for display/audit only.
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TokenType.ACCESS.name());
        claims.put("tokenVersion", user.getTokenVersion());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole().toString());
        logger.debug("Generating ACCESS token for publicID: {}", user.getPublicID());
        return buildToken(claims, user.getPublicID(), accessExpiration);
    }

    /**
     * Generate refresh token using publicID as subject. Username is included for display/audit only.
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TokenType.REFRESH.name());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole().toString());
        logger.debug("Generating REFRESH token for publicID: {}", user.getPublicID());
        return buildToken(claims, user.getPublicID(), refreshExpiration);
    }

    private String buildToken(Map<String, Object> claims,
                              String subject,
                              long expiration) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ============================
    // VALIDATION
    // ============================

    /**
     * Validate access token: subject must match user publicID, not expired, type ACCESS.
     * Username claim is ignored for validation.
     */
    public boolean isTokenValid(String token, User user) {
        final String subject = extractSubject(token);
        boolean valid = subject.equals(user.getPublicID())
                && !isTokenExpired(token)
                && isAccessToken(token);
        if (!valid) {
            logger.warn("Rejected ACCESS token for publicID: {}", subject);
        }
        return valid;
    }

    /**
     * Validate refresh token: subject must match user publicID, not expired, type REFRESH.
     * Username claim is ignored for validation.
     */
    public boolean isRefreshTokenValid(String token, User user) {
        final String subject = extractSubject(token);
        boolean valid = subject.equals(user.getPublicID())
                && !isTokenExpired(token)
                && isRefreshToken(token);
        if (!valid) {
            logger.warn("Rejected REFRESH token for publicID: {}", subject);
        }
        return valid;
    }

    // ============================
    // CLAIM EXTRACTION
    // ============================

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }
    public Integer isTokenVersionValid(String token) {
        return extractClaim(token, claims -> claims.get("tokenVersion", Integer.class));
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ============================
    // UTILITIES
    // ============================

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private boolean isAccessToken(String token) {
        return TokenType.ACCESS.name().equals(extractTokenType(token));
    }

    private boolean isRefreshToken(String token) {
        return TokenType.REFRESH.name().equals(extractTokenType(token));
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
// Security improvement: Only the 'sub' (publicID) claim is used for authentication and validation. The 'username' claim is for display/audit only, so username changes do not invalidate JWTs.