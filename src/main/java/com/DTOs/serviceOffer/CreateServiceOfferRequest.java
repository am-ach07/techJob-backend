package com.DTOs.serviceOffer;

public class CreateServiceOfferRequest {

	private CreateOfferDTO offerDto;
	private CreateExtraServiceOfferDTO extraDto;
	
	
	
	
	
	public CreateOfferDTO getOfferDto() {
		return offerDto;
	}
	public void setOfferDto(CreateOfferDTO offerDto) {
		this.offerDto = offerDto;
	}
	public CreateExtraServiceOfferDTO getExtraDto() {
		return extraDto;
	}
	public void setExtraDto(CreateExtraServiceOfferDTO extraDto) {
		this.extraDto = extraDto;
	}
	
	
	
	
	
	
}
