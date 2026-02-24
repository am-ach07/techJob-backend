package com.techJob.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.techJob.domain.enums.Category;
import com.techJob.domain.enums.OffersStatus;

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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_offers")
public class ServiceOffer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	private String description;
	@OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("position ASC")
	private List<Image> images = new ArrayList<>();

	@Column(name = "price", nullable = false)
	private BigDecimal price;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "artisan_id", nullable = false)
	private ArtisanProfile artisan;
	@Column(updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
	
	private LocalDateTime deletedAt;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private OffersStatus offersStatus;
	@Column(nullable = false, unique = true, updatable = false)
	private String offerPublicID;
	@Enumerated(EnumType.STRING)
	private Category category;
	@OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
	private List<Order> orders = new ArrayList<>();

	@OneToMany(mappedBy = "serviceOffer", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ExtraServiceOffer> extraServiceOffers = new HashSet<>();

	// ===== Helper Methods =====

	public void addOrder(Order order) {
		orders.add(order);
		order.setService(this);
	}

	public void removeOrder(Order order) {
		orders.remove(order);
		order.setService(null);
	}

	public void addImage(Image image) {
		image.setOffer(this);
		this.images.add(image);
	}

	public void removeImage(Image image) {
		this.images.remove(image);
		image.setOffer(null);
	}

	public void removeExtraService(ExtraServiceOffer extra) {
	    extraServiceOffers.remove(extra);
	    extra.setServiceOffer(null);
	}

	
	
	
	
	
	
	// ===== Getters & Setters =====

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public ArtisanProfile getArtisan() {
		return artisan;
	}

	public void setArtisan(ArtisanProfile artisan) {
		this.artisan = artisan;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getOfferPublicID() {
		return offerPublicID;
	}

	public void setOfferPublicID(String offerPublicID) {
		this.offerPublicID = offerPublicID;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public Set<ExtraServiceOffer> getExtraServiceOffers() {
		return extraServiceOffers;
	}

	public void setExtraServiceOffers(Set<ExtraServiceOffer> extraServiceOffers) {
		this.extraServiceOffers = extraServiceOffers;
	}

	

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	public OffersStatus getOffersStatus() {
		return offersStatus;
	}

	public void setOffersStatus(OffersStatus offersStatus) {
		this.offersStatus = offersStatus;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	

}