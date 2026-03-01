package com.techJob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.User;
import com.techJob.domain.entity.Wallet;

public interface WalletRepositrory extends JpaRepository<Wallet, Long> {

	Optional<Wallet> findByUser(User user);

}
