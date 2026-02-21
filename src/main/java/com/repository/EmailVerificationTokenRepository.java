package com.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.domain.entity.EmailVerificationToken;
import com.domain.entity.User;

public interface EmailVerificationTokenRepository 
extends JpaRepository<EmailVerificationToken, Long> {

		Optional<EmailVerificationToken> findByToken(String token);
		Optional<EmailVerificationToken> findByUser(User user);
		EmailVerificationToken findLatestByUser(User user);
		void deleteByUser(User user);
		int deleteByExpiryDateBefore(LocalDateTime time);

}
