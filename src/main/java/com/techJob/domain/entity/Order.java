package com.techJob.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.techJob.domain.enums.DepositStatus;
import com.techJob.domain.enums.FinalStatus;
import com.techJob.domain.enums.OrdersStatus;
import com.techJob.domain.enums.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;
    @Column(nullable = false, unique = true, updatable = false)
    private String orderPublicID;
    private BigDecimal totalPriceSnapshot;
    private BigDecimal offerPriceSnapshot;
    private String offerTitleSnapshot;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceOffer service;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
   
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrdersStatus status = OrdersStatus.PENDING_ARTISAN_ACCEPTANCE;
    
    @OneToMany(mappedBy="order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderExtra> extras = new HashSet<>();
    
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal depositAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepositStatus depositStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinalStatus finalStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
	private List<Payment> payment;

    
    
    
    // ===== Getters & Setters =====

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getClient() {
		return client;
	}

	public void setClient(User client) {
		this.client = client;
	}

	public ServiceOffer getService() {
		return service;
	}

	public void setService(ServiceOffer service) {
		this.service = service;
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

	

	public Set<OrderExtra> getExtras() {
		return extras;
	}

	public void setExtras(Set<OrderExtra> extras) {
		this.extras = extras;
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

	public BigDecimal getTotalPriceSnapshot() {
		return totalPriceSnapshot;
	}

	public void setTotalPriceSnapshot(BigDecimal totalPriceSnapshot) {
		this.totalPriceSnapshot = totalPriceSnapshot;
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

	public List<Payment> getPayments() {
		return payment;
	}

	public void setPayments(List<Payment> payment) {
		this.payment = payment;
	}

	public List<Payment> getPayment() {
		return payment;
	}

	public void setPayment(List<Payment> payment) {
		this.payment = payment;
	}

	
	
		
	
	

	
	
	
	
    
    
}