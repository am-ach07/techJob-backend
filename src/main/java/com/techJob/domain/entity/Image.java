package com.techJob.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(nullable = false, unique = true, updatable = false)
    private String imagePublicID;
    @Column(nullable = false)
    private String url;

    private Integer position; 
    // null for profile image

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /* ========== Relations ========== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private ServiceOffer offer;


    // getters setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public ServiceOffer getOffer() {
		return offer;
	}

	public void setOffer(ServiceOffer offer) {
		this.offer = offer;
	}

	

	public String getImagePublicID() {
		return imagePublicID;
	}

	public void setImagePublicID(String imagePublicID) {
		this.imagePublicID = imagePublicID;
	}
  
	
    
    

    
    
 
    
}

