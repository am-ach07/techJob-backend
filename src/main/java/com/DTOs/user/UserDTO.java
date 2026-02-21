package com.DTOs.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.domain.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;

public class UserDTO  {

	
	private String firstName;
    private String lastName;
    private String username;
    private String email;
    private AddressDTO address;
    private String phoneNumber;
    private Gender gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    private String profilImageUrl;
    private String publicID;
    private LocalDateTime createdAt ;
	
	//getter and setter
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPublicID() {
		return publicID;
	}
	public void setPublicID(String publicID) {
		this.publicID = publicID;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public AddressDTO getAddress() {
		return address;
	}
	public void setAddress(AddressDTO address) {
		this.address = address;
	}
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getProfilImageUrl() {
		return profilImageUrl;
	}
	public void setProfilImageUrl(String profilImageUrl) {
		this.profilImageUrl = profilImageUrl;
	}

	
	
	
	
	
	
}
