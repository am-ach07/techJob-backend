package com.techJob.DTOs.serviceOffer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.techJob.DTOs.image.ImageDTO;
import com.techJob.domain.enums.Category;
import com.techJob.domain.enums.OffersStatus;
import com.techJob.response.ProfileResponse;

public class ServiceOfferDTO {

	
	private String title;
	private String description;
	private List<ImageDTO> images;
	private BigDecimal price;
	private ProfileResponse profile;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private OffersStatus offersStatus;
    private String offerPublicID;
    private Category category;
    private Set<ExtraServiceOfferDTO> extraServiceOffers;

    //===========helper Methodes======================
    public void addServiceExtras(ExtraServiceOfferDTO extraService) {
    	this.extraServiceOffers.add(extraService);
    }
    public void addImages(ImageDTO image) {
    	this.images.add(image);
    }
    
    
    
    
	// Getters and Setters
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

	
	
	
	
	
	public List<ImageDTO> getImages() {
		return images;
	}
	public void setImages(List<ImageDTO> images) {
		this.images = images;
	}
	
	public ProfileResponse getProfile() {
		return profile;
	}
	public void setProfile(ProfileResponse profile) {
		this.profile = profile;
	}
	
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	
	public OffersStatus getOffersStatus() {
		return offersStatus;
	}
	public void setOffersStatus(OffersStatus offersStatus) {
		this.offersStatus = offersStatus;
	}
	public Set<ExtraServiceOfferDTO> getExtraServiceOffers() {
		return extraServiceOffers;
	}
	public void setExtraServiceOffers(Set<ExtraServiceOfferDTO> extraServiceOffers) {
		this.extraServiceOffers = extraServiceOffers;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getOfferPublicID() {
		return offerPublicID;
	}
	public void setOfferPublicID(String offerPublicID) {
		this.offerPublicID = offerPublicID;
	}
	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}
	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
	
	
	
}