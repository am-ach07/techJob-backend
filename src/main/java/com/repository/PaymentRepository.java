package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.domain.entity.Order;
import com.domain.entity.Payment;
import com.domain.entity.User;



public interface PaymentRepository extends JpaRepository<Payment,Long> {

	
	Optional<Payment> findByPaymentPublicID(String paymentPublicID); 
	
	Optional<Payment> findByOrder(Order order);
}
