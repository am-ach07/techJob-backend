package com.techJob.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Wallet {

	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    @Column(nullable = false, unique = true, updatable = false)
	private String walletsPublicID;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;
	private BigDecimal balance = BigDecimal.ZERO;
	private BigDecimal pendingBalance = BigDecimal.ZERO;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getWalletsPublicID() {
		return walletsPublicID;
	}
	public void setWalletsPublicID(String walletsPublicID) {
		this.walletsPublicID = walletsPublicID;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getPendingBalance() {
		return pendingBalance;
	}
	public void setPendingBalance(BigDecimal pendingBalance) {
		this.pendingBalance = pendingBalance;
	}

	
	
	
	
	
	
	
}
