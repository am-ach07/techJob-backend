package com.domain.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "artisan_profiles")
public class ArtisanProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private String description;
	@Column(nullable = false)
	private Double averageRating = 0.0;

	@Column(nullable = false)
	private Integer ratingCount = 0;
	private Integer completedOrder = 0;
	private Boolean verified = false;
	@OneToMany(mappedBy = "artisan", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ArtisanSkill> skills = new ArrayList<>();

	@OneToMany(mappedBy = "artisan", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ServiceOffer> serviceOffers = new ArrayList<>();

	// ===== Helper Methods =====

	public void addService(ServiceOffer service) {
		serviceOffers.add(service);
		service.setArtisan(this);
	}

	public void removeService(ServiceOffer service) {
		serviceOffers.remove(service);
		service.setArtisan(null);
	}
	public void addSkills(List<ArtisanSkill> skills) {
		
		this.skills.addAll(skills);
		
	}
	public void removeSkills(List<ArtisanSkill> skills) {
		this.skills.removeAll(skills);
	}
	
	// ===== Getters & Setters =====

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	public List<ArtisanSkill> getSkills() {
		return skills;
	}

	public void setSkills(List<ArtisanSkill> skills) {
		this.skills = skills;
	}

	public List<ServiceOffer> getServiceOffers() {
		return serviceOffers;
	}

	public void setServiceOffers(List<ServiceOffer> serviceOffers) {
		this.serviceOffers = serviceOffers;
	}

	

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public Integer getRatingCount() {
		return ratingCount;
	}

	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}

	public Boolean getVerifyed() {
		return verified;
	}

	public void setVerifyed(Boolean verified) {
		this.verified = verified;
	}

	public Integer getCompletedOrder() {
		return completedOrder;
	}

	public void setCompletedOrder(Integer completedOrder) {
		this.completedOrder = completedOrder;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}
	
	
}
