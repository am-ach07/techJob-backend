package com.DTOs.user;

import java.time.LocalDate;

import com.domain.enums.Gender;

public class UpdateUserDTO {
 
	    
	private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
	    
	    
	    //getter and setter
		
		
		
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
		
		public Gender getGender() {
			return gender;
		}
		public void setGender(Gender gender) {
			this.gender = gender;
		}
		public LocalDate getDateOfBirth() {
			return dateOfBirth;
		}
		public void setDateOfBirth(LocalDate dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}
		
		
	    
	    
	    
}
