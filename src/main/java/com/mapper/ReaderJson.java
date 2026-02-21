package com.mapper;

import org.springframework.stereotype.Service;

import com.DTOs.serviceOffer.CreateServiceOfferRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;



@Service
public class ReaderJson {

	
	
	
	
	public CreateServiceOfferRequest readJsonCreating(String json)throws  JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();

	    CreateServiceOfferRequest dto =
	            mapper.readValue(json, CreateServiceOfferRequest.class);
	    return dto;
	}
	
}
