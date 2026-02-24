package com.techJob.DTOs.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.techJob.DTOs.order.OrderDTO;
import com.techJob.DTOs.user.UserDTO;
import com.techJob.domain.enums.PaymentStatus;
import com.techJob.domain.enums.PaymentType;

public class PaymentDTO {

	
	
	
	
	private String paymentPublicID;
	private BigDecimal amount;
	private PaymentStatus paymentStatus;
	private PaymentType paymentType;
	private LocalDateTime createdAt;
	private LocalDateTime paidAt;
	private String paymentUrl;	
	
	
	
	//getter and setter
	public String getPaymentPublicID() {
		return paymentPublicID;
	}
	public void setPaymentPublicID(String paymentPublicID) {
		this.paymentPublicID = paymentPublicID;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getPaidAt() {
		return paidAt;
	}
	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}
	public String getPaymentUrl() {
		return paymentUrl;
	}
	public void setPaymentUrl(String paymentUrl) {
		this.paymentUrl = paymentUrl;
	}
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	
	
	
	
	
	
}

