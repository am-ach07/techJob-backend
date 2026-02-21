package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.domain.entity.Wallet;

public interface WalletRepositrory extends JpaRepository<Wallet, Long> {

}
