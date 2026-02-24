package com.techJob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.RefreshToken;
import com.techJob.domain.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	void deleteByUser(User user);
	Optional<RefreshToken>  findByToken(String token);
	int deleteByExpiryDateBefore(java.time.LocalDateTime time);
	Optional<RefreshToken> findByUser(User user);
}
