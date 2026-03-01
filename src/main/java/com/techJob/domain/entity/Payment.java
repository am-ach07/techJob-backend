package com.techJob.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.techJob.domain.enums.PaymentStatus;
import com.techJob.domain.enums.PaymentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Payment {

	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    @Column(nullable = false, unique = true, updatable = false)
	private String paymentPublicID;
	private BigDecimal amount;
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private PaymentType paymentType;
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private PaymentStatus paymentStatus;
	@Column(updatable = false)
	private LocalDateTime createdAt=LocalDateTime.now();
	private LocalDateTime paidAt;
	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;
	private Boolean processed=false;
	
	
	
	
	///getter and setter
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
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
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public Boolean getProcessed() {
		return processed;
	}
	public void setProcessed(Boolean processed) {
		this.processed = processed;		
	}
	

	
	
	
	
	
	
	
}
