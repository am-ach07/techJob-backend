package com.DTOs;

import jakarta.validation.constraints.Size;

public class SearchPatientDTO extends PaginationAndSortDTO {
	@Size(min = 2, max = 50, message = "name should be between 2 and 50")
	private String name;
	

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	

}
