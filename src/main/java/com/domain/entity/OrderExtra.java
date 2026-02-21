package com.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	    name = "order_extras",
	    uniqueConstraints = {
	        @UniqueConstraint(columnNames = {"order_id", "extra_id"})
	    }
	)
public class OrderExtra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
	private Order order;

	 @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "extra_id", nullable = false)
	private ExtraServiceOffer extra;
	 @Column(nullable = false, unique = true, updatable = false)
	    private String orderExtraPublicID;
	private String extraTitleSnapshot;
	private BigDecimal extraPriceSnapshot;

	//==========helper Methods==================
	
	
	
	//=========getter and setter================
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public ExtraServiceOffer getExtra() {
		return extra;
	}

	public void setExtra(ExtraServiceOffer extra) {
		this.extra = extra;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExtraTitleSnapshot() {
		return extraTitleSnapshot;
	}

	public void setExtraTitleSnapshot(String extraTitleSnapshot) {
		this.extraTitleSnapshot = extraTitleSnapshot;
	}

	public BigDecimal getExtraPriceSnapshot() {
		return extraPriceSnapshot;
	}

	public void setExtraPriceSnapshot(BigDecimal extraPriceSnapshot) {
		this.extraPriceSnapshot = extraPriceSnapshot;
	}

	public String getOrderExtraPublicID() {
		return orderExtraPublicID;
	}

	public void setOrderExtraPublicID(String orderExtraPublicID) {
		this.orderExtraPublicID = orderExtraPublicID;
	}

	

	

}
