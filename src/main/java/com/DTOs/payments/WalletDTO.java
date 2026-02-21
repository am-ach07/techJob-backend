package com.DTOs.payments;

import java.math.BigDecimal;

import com.DTOs.user.UserDTO;

public class WalletDTO {

	
	
	private String walletsPublicID;
	private UserDTO user;
	private BigDecimal balance ;
	private BigDecimal pendingBalance ;
	
	
	
	
	
	
	//getter and setter
	public String getWalletsPublicID() {
		return walletsPublicID;
	}
	public void setWalletsPublicID(String walletsPublicID) {
		this.walletsPublicID = walletsPublicID;
	}
	public UserDTO getUser() {
		return user;
	}
	public void setUser(UserDTO user) {
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
