package com.techJob.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techJob.response.ApiResponse;
import com.techJob.response.ApiResponseFactory;
import com.techJob.service.PaymentService;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentRestController {

	private final PaymentService paymentService ;
	
	
	
	
	public PaymentRestController(PaymentService paymentService) {
		super();
		this.paymentService = paymentService;
	}




	@PostMapping("/mock/{paymentId}")
	public ResponseEntity<ApiResponse<String>> mockPay(
			@PathVariable String paymentId) {
	    paymentService.confirmDepositPayment(paymentId);
	    return ApiResponseFactory.ok(null,"Payment successful");
	}

	
}
