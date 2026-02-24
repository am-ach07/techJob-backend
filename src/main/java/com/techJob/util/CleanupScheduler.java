package com.techJob.util;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techJob.domain.enums.OffersStatus;
import com.techJob.repository.EmailVerificationTokenRepository;
import com.techJob.repository.RefreshTokenRepository;
import com.techJob.repository.ServiceOfferRepository;

@Component
public class CleanupScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(CleanupScheduler.class);

    private static final int OFFER_RETENTION_DAYS = 30;

    private final EmailVerificationTokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ServiceOfferRepository serviceOfferRepository;

    public CleanupScheduler(
            EmailVerificationTokenRepository tokenRepository,
            RefreshTokenRepository refreshTokenRepository,
            ServiceOfferRepository serviceOfferRepository) {

        this.tokenRepository = tokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.serviceOfferRepository = serviceOfferRepository;
    }

    // =========================================
    // Email Verification Token Cleanup
    // Runs every day at 02:00 AM
    // =========================================
    @Transactional
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredEmailTokens() {

        LocalDateTime now = LocalDateTime.now();

        int deleted =
                tokenRepository.deleteByExpiryDateBefore(now);

        logger.info("[Cleanup] Email tokens deleted: {}", deleted);
    }

    // =========================================
    // Refresh Token Cleanup
    // Runs every day at 02:30 AM
    // =========================================
    @Transactional
    @Scheduled(cron = "0 30 2 * * ?")
    public void cleanupExpiredRefreshTokens() {

        LocalDateTime now = LocalDateTime.now();

        int deleted =
                refreshTokenRepository.deleteByExpiryDateBefore(now);

        logger.info("[Cleanup] Refresh tokens deleted: {}", deleted);
    }

    // =========================================
    // Soft Deleted Offers Cleanup
    // Runs every day at 03:00 AM
    // =========================================
    @Transactional
    @Scheduled(cron = "0 0 3 * * ?") // كل يوم 3 صباحا
    public void cleanupArchivedOffers() {

        LocalDateTime limit = LocalDateTime.now().minusDays(30);

        int deleted = serviceOfferRepository
                .deleteByOffersStatusAndDeletedAtBefore(OffersStatus.ARCHIVED, limit);

        logger.info("Cleanup: permanently deleted {} archived offers", deleted);
    }
    



}
