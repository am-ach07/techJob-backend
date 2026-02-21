package com.DTOs.user;

import com.domain.enums.Commune;
import com.domain.enums.Daïras;
import com.domain.enums.Nationality;
import com.domain.enums.Wilaya;

public class AddressDTO {

	

	private Nationality nationality;
	private Wilaya wilaya;
    private Daïras Daïras;
    private Commune Commune;
    private String street;
    private Integer postalCode;
	public Nationality getNationality() {
		return nationality;
	}
	public void setNationality(Nationality nationality) {
		this.nationality = nationality;
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
    
    
    
    
    
    
    
}
