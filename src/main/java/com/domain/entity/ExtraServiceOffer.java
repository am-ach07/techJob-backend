package com.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ExtraServiceOffer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String title;
	@Column(nullable = false, unique = true, updatable = false)
	private String extraOfferPublicID;
	@Column(nullable = false)
	private BigDecimal price;


	@ManyToOne
	@JoinColumn(name = "service_offer_id")
	private ServiceOffer serviceOffer;

	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public ServiceOffer getServiceOffer() {
		return serviceOffer;
	}

	public void setServiceOffer(ServiceOffer serviceOffer) {
		this.serviceOffer = serviceOffer;
	}

	public String getExtraOfferPublicID() {
		return extraOfferPublicID;
	}

	public void setExtraOfferPublicID(String extraOfferPublicID) {
		this.extraOfferPublicID = extraOfferPublicID;
	}

	
	

	
}