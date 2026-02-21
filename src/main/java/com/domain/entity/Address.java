package com.domain.entity;

import com.domain.enums.Commune;
import com.domain.enums.Daïras;
import com.domain.enums.Nationality;
import com.domain.enums.Wilaya;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="addresses")
public class Address {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 70)
	private Nationality nationality;
	@Column(nullable = false, length = 70)
	@Enumerated(EnumType.STRING)
	private Wilaya wilaya;
	@Column(nullable = false, length = 70)
	@Enumerated(EnumType.STRING)
    private Daïras Daïras;
	@Column(nullable = false, length = 70)
	@Enumerated(EnumType.STRING)
    private Commune Commune;
    private String street;
    private Integer postalCode;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    
    // ===== Getters & Setters =====
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Nationality getNationality() {
		return nationality;
	}
	public void setNationality(Nationality nationality) {
		this.nationality = nationality;
	}
	
	
	
	public Commune getCommune() {
		return Commune;
	}
	public void setCommune(Commune commune) {
		Commune = commune;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public Integer getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(Integer postalCode) {
		this.postalCode = postalCode;
	}
	public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
	public Wilaya getWilaya() {
		return wilaya;
	}
	public void setWilaya(Wilaya wilaya) {
		this.wilaya = wilaya;
	}
	public Daïras getDaïras() {
		return Daïras;
	}
	public void setDaïras(Daïras daïras) {
		Daïras = daïras;
	}
	
	
}