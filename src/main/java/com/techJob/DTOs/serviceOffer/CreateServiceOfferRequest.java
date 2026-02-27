package com.techJob.DTOs.serviceOffer;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CreateServiceOfferRequest {
	
	@Valid
	@NotNull(message = "you didn't enser an offer!!!")
	private CreateOfferDTO offerDto;
	@Valid
	private Set<CreateExtraServiceOfferDTO> extraDto;
	
	
	
	
	
	public CreateOfferDTO getOfferDto() {
		return offerDto;
	}
	public void setOfferDto(CreateOfferDTO offerDto) {
		this.offerDto = offerDto;
	}
	public Set<CreateExtraServiceOfferDTO> getExtraDto() {
		return extraDto;
	}
	public void setExtraDto(Set<CreateExtraServiceOfferDTO> extraDto) {
		this.extraDto = extraDto;
	}
	
	
	
	
	
	
}
