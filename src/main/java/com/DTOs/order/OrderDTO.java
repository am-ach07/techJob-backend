package com.DTOs.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.DTOs.payments.PaymentDTO;
import com.DTOs.user.UserDTO;
import com.domain.entity.OrderExtra;
import com.domain.entity.Payment;
import com.domain.entity.ServiceOffer;
import com.domain.enums.DepositStatus;
import com.domain.enums.FinalStatus;
import com.domain.enums.OrdersStatus;
import com.domain.enums.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

public class OrderDTO {

	private UserDTO client;
	private String orderPublicID;
    private BigDecimal totalPriceSnapshot;
    private BigDecimal offerPriceSnapshot;
    private String offerTitleSnapshot;
    private LocalDateTime createdAt ;
   
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    
   
    private OrdersStatus status;
    private Set<OrderExtraDTO> extras;
    private BigDecimal depositAmount;
    private BigDecimal finalAmount;
    private DepositStatus depositStatus;
    private FinalStatus finalStatus;
    private PaymentStatus paymentStatus;
	private List<PaymentDTO> payment;
	private String offerPath;
	
	
    

	// Getters and Setters
	public UserDTO getClient() {
		return client;
	}

	public void setClient(UserDTO client) {
		this.client = client;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public OrdersStatus getStatus() {
		return status;
	}

	public void setStatus(OrdersStatus status) {
		this.status = status;
	}

	public String getOrderPublicID() {
		return orderPublicID;
	}

	public void setOrderPublicID(String orderPublicID) {
		this.orderPublicID = orderPublicID;
	}

	public LocalDateTime getAcceptedAt() {
		return acceptedAt;
	}

	public void setAcceptedAt(LocalDateTime acceptedAt) {
		this.acceptedAt = acceptedAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

	public LocalDateTime getCancelledAt() {
		return cancelledAt;
	}

	public void setCancelledAt(LocalDateTime cancelledAt) {
		this.cancelledAt = cancelledAt;
	}

	public BigDecimal getTotalPriceSnapshot() {
		return totalPriceSnapshot;
	}

	public void setTotalPriceSnapshot(BigDecimal totalPriceSnapshot) {
		this.totalPriceSnapshot = totalPriceSnapshot;
	}

	public BigDecimal getOfferPriceSnapshot() {
		return offerPriceSnapshot;
	}

	public void setOfferPriceSnapshot(BigDecimal offerPriceSnapshot) {
		this.offerPriceSnapshot = offerPriceSnapshot;
	}

	public String getOfferTitleSnapshot() {
		return offerTitleSnapshot;
	}

	public void setOfferTitleSnapshot(String offerTitleSnapshot) {
		this.offerTitleSnapshot = offerTitleSnapshot;
	}

	public String getOfferPath() {
		return offerPath;
	}

	public void setOfferPath(String offerPath) {
		this.offerPath = offerPath;
	}

	public Set<OrderExtraDTO> getExtras() {
		return extras;
	}

	public void setExtras(Set<OrderExtraDTO> extras) {
		this.extras = extras;
	}

	public List<PaymentDTO> getPayment() {
		return payment;
	}

	public void setPayment(List<PaymentDTO> payment) {
		this.payment = payment;
	}

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

	public BigDecimal getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(BigDecimal finalAmount) {
		this.finalAmount = finalAmount;
	}

	public DepositStatus getDepositStatus() {
		return depositStatus;
	}

	public void setDepositStatus(DepositStatus depositStatus) {
		this.depositStatus = depositStatus;
	}

	public FinalStatus getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(FinalStatus finalStatus) {
		this.finalStatus = finalStatus;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	

	
}