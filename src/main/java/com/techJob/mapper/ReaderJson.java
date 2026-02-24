package com.techJob.mapper;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techJob.DTOs.serviceOffer.CreateServiceOfferRequest;



@Service
public class ReaderJson {

	
	
	
	
	public CreateServiceOfferRequest readJsonCreating(String json)throws  JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

	    CreateServiceOfferRequest dto =
	            mapper.readValue(json, CreateServiceOfferRequest.class);
	    return dto;
	}
	
}
