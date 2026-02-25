package com.techJob.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.techJob.DTOs.payments.PaymentDTO;
import com.techJob.domain.entity.Order;
import com.techJob.domain.entity.Payment;
import com.techJob.domain.entity.User;
import com.techJob.domain.enums.DepositStatus;
import com.techJob.domain.enums.OrdersStatus;
import com.techJob.domain.enums.PaymentStatus;
import com.techJob.domain.enums.PaymentType;
import com.techJob.domain.enums.Roles;
import com.techJob.exception.emailVerification.AccountNotVerifiedException;
import com.techJob.exception.order.InvalidOrderException;
import com.techJob.exception.payment.PaymentAlreadyExistException;
import com.techJob.exception.payment.PaymentException;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.mapper.GeneralMapper;
import com.techJob.repository.OrderRepository;
import com.techJob.repository.PaymentRepository;
import com.techJob.repository.UserRepository;
import com.techJob.repository.WalletRepositrory;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

	
	
	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

	
	private final PaymentRepository paymentRepository;
	private final WalletRepositrory walletRepositrory;
	private final UserRepository userRepository; 
	private final GeneralMapper generalMapper;
	private final OrderRepository orderRepository;
	@Value("${spring.payment.url}")
    private String paymentUrl;
	
	
	public PaymentService(
			PaymentRepository paymentRepository,
			WalletRepositrory walletRepositrory,
			UserRepository userRepository,
			GeneralMapper generalMapper,
			OrderRepository orderRepository) {
		super();
		this.paymentRepository = paymentRepository;
		this.walletRepositrory = walletRepositrory;
		this.userRepository = userRepository;
		this.generalMapper = generalMapper;
		this.orderRepository = orderRepository;
	}
	
	
	
	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName();

	    return userRepository
	            .findByUsernameOrEmail(email, email)
	            .orElseThrow(() -> new UserNotFoundException(email));
	}
	//verify admin role
	 private void verficationAdmin(User user) {
			if(!user.getRole().equals(Roles.ADMIN)) {
				throw new InvalidOrderException("You are not allowed to view this order");
			}
		}
	 //verify email verification
	private void verificationEmail(User user) {
		if(!user.getEmailVerified())
	    	throw new AccountNotVerifiedException("Please verify your email to view your orders.");
	    
	}
	
	
	//paid 30%of price 
	@Transactional
	public PaymentDTO createDepositPayment(Order order) {

	    User user = getCurrentUser();

	    // 1. منع الدفع المكرر
	    if (order.getDepositStatus() == DepositStatus.PAID) {
	        throw new PaymentAlreadyExistException("Deposit already paid");
	    }

	    // 2. التأكد من حالة الطلب
	    if (order.getPaymentStatus() != PaymentStatus.UNPAID) {
	        throw new PaymentException("Invalid payment state");
	    }

	    // 3. استخدام المبلغ المخزن داخل order
	    BigDecimal deposit = order.getDepositAmount();

	    // حماية إضافية
	    if (deposit == null || deposit.compareTo(BigDecimal.ZERO) <= 0) {
	        throw new PaymentException("Invalid deposit amount");
	    }

	    // 4. إنشاء payment
	    Payment payment = new Payment();
	    payment.setAmount(deposit);
	    payment.setPaymentType(PaymentType.DEPOSIT);
	    payment.setPaymentStatus(PaymentStatus.PENDING);
	    payment.setPaymentPublicID(UUID.randomUUID().toString());
	    payment.setOrder(order);

	    paymentRepository.save(payment);

	    log.info("User {} initiated deposit {} for order {}",
	            user.getUsername(),
	            deposit,
	            order.getOrderPublicID());

	    PaymentDTO dto = generalMapper.toDTO(payment);

	    dto.setPaymentUrl(paymentUrl + payment.getPaymentPublicID());

	    return dto;

	}
	
	
	@Transactional
	public PaymentDTO confirmDepositPayment(String paymentPublicID) {

	    Payment payment = paymentRepository.findByPaymentPublicID(paymentPublicID)
	            .orElseThrow(() -> new PaymentException("Payment Not Found"));

	    if (payment.getPaymentStatus() != PaymentStatus.PENDING)
	        throw new PaymentException("Invalid payment state");

	    if (payment.getPaymentType() != PaymentType.DEPOSIT)
	        throw new PaymentException("Invalid payment type");

	    Order order = payment.getOrder();

	    payment.setPaymentStatus(PaymentStatus.PAID);
	    payment.setPaidAt(LocalDateTime.now());

	    order.setDepositStatus(DepositStatus.PAID);
	    order.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
	    order.setStatus(OrdersStatus.IN_PROGRESS);

	    paymentRepository.save(payment);
	    orderRepository.save(order);

	    return generalMapper.toDTO(payment);
	}


	
	
	
	
	
	
}
