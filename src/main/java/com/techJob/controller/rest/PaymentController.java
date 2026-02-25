package com.techJob.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techJob.response.ApiResponse;
import com.techJob.response.ApiResponseFactory;
import com.techJob.service.PaymentService;

@Controller
@RequestMapping("/api/v1/payment")
public class PaymentController {

	private final PaymentService paymentService ;
	
	
	
	
	public PaymentController(PaymentService paymentService) {
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
