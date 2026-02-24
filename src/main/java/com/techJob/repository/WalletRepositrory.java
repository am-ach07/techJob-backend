package com.techJob.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.Wallet;

public interface WalletRepositrory extends JpaRepository<Wallet, Long> {

}
