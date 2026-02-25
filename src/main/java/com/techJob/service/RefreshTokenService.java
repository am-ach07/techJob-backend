package com.techJob.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techJob.domain.entity.RefreshToken;
import com.techJob.domain.entity.User;
import com.techJob.exception.auth.RefreshTokenException;
import com.techJob.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDuration;

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // ============================
    // CREATE (Login)
    // ============================

    /**
     * Create a new refresh token for the user, deleting any previous tokens (rotation).
     * Uses publicID for all associations.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // one active refresh per user (recommended)
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user); // user.getPublicID() is used in JWTs and can be used in DB if needed
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(refreshTokenDuration)
        );
        logger.info("Created new refresh token for publicID: {}", user.getPublicID());
        return refreshTokenRepository.save(refreshToken);
    }

    // ============================
    // VERIFY (Before Refresh)
    // ============================

    /**
     * Verify refresh token validity and expiry. Delete if expired (replay protection).
     */
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RefreshTokenException("Invalid refresh token"));
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            logger.warn("Expired refresh token used for publicID: {}", refreshToken.getUser().getPublicID());
            throw new RefreshTokenException("Refresh token expired");
        }
        return refreshToken;
    }
    
    
    
    

    // ============================
    // ROTATION (Security Core)
    // ============================

    /**
     * Rotate refresh token: delete old, issue new (prevents replay).
     */
    @Transactional
    public RefreshToken rotateRefreshToken(String oldToken) {
        RefreshToken currentToken = verifyRefreshToken(oldToken);
        User user = currentToken.getUser();
        // revoke old token
        refreshTokenRepository.delete(currentToken);
        logger.info("Rotated refresh token for publicID: {}", user.getPublicID());
        // issue new token
        return createRefreshToken(user);
    }

    // ============================
    // LOGOUT
    // ============================

    /**
     * Delete all refresh tokens for a user (logout everywhere).
     */
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
        logger.info("Deleted all refresh tokens for publicID: {}", user.getPublicID());
    }
    
    
    
    
}