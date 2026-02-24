package com.techJob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.Order;
import com.techJob.domain.entity.Payment;
import com.techJob.domain.entity.User;



public interface PaymentRepository extends JpaRepository<Payment,Long> {

	
	Optional<Payment> findByPaymentPublicID(String paymentPublicID); 
	
	Optional<Payment> findByOrder(Order order);
}
