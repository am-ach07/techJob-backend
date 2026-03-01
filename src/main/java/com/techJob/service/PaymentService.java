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
import com.techJob.domain.entity.Wallet;
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
	private final WalletService walletService;
	
	@Value("${spring.payment.url}")
    private String paymentUrl;


	
	
	public PaymentService(
			PaymentRepository paymentRepository,
			WalletRepositrory walletRepositrory,
			UserRepository userRepository,
			GeneralMapper generalMapper,
			OrderRepository orderRepository,
			WalletService walletService) {
		super();
		this.paymentRepository = paymentRepository;
		this.walletRepositrory = walletRepositrory;
		this.userRepository = userRepository;
		this.generalMapper = generalMapper;
		this.orderRepository = orderRepository;
		this.walletService = walletService;
	}
	
	
	
	private User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName();

	    return userRepository
	            .findByUsernameOrEmail(email, email)
	            .orElseThrow(() -> new UserNotFoundException(email));
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

	    
	 // 🔐 الحماية من التكرار
	    if (Boolean.TRUE.equals(payment.getProcessed())) {
	        throw new PaymentException("Payment already processed");
	    }
	    
	    
	    Order order = payment.getOrder();

	    
	    if (!true) { // Simulate payment gateway response
	        payment.setPaymentStatus(PaymentStatus.FAILED);
	        paymentRepository.save(payment);
	        return generalMapper.toDTO(payment);
	    } 
	    	
	    payment.setPaymentStatus(PaymentStatus.PAID);
	    payment.setPaidAt(LocalDateTime.now());
	    payment.setProcessed(true); // Mark as processed to prevent duplicates
	    
	    order.setDepositStatus(DepositStatus.PAID);
	    order.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
	    order.setStatus(OrdersStatus.IN_PROGRESS);

	    Wallet platformWallet = walletService.getPlatformWallet();
	    
	 // 🔐 تنفيذ escrow
	    walletService.freezeToEscrow(
	            platformWallet,
	            payment.getAmount(),
	            payment.getOrder().getOrderPublicID()
	    );
	    
	    paymentRepository.save(payment);
	    orderRepository.save(order);

	    return generalMapper.toDTO(payment);
	}

	@Transactional
	public PaymentDTO createFinalPayment(Order order) {

	    if (order.getDepositStatus() != DepositStatus.PAID) {
	        throw new PaymentException("Deposit not paid yet");
	    }
	    if(paymentRepository.existsByOrderAndPaymentType(order, PaymentType.FINAL_PAYMENT)){
	        throw new PaymentException("Final payment already created");
	    }
	    BigDecimal remainingAmount = order.getTotalPriceSnapshot()
	            .subtract(order.getDepositAmount());

	    Payment payment = new Payment();
	    payment.setAmount(remainingAmount);
	    payment.setPaymentType(PaymentType.FINAL_PAYMENT);
	    payment.setPaymentStatus(PaymentStatus.PENDING);
	    payment.setPaymentPublicID(UUID.randomUUID().toString());
	    payment.setOrder(order);

	    paymentRepository.save(payment);

	    PaymentDTO dto = generalMapper.toDTO(payment);
	    dto.setPaymentUrl(paymentUrl + payment.getPaymentPublicID());

	    return dto;
	}
	
	
	@Transactional
	public void finalizeOrderPayment(Order order) {

	    if (order.getStatus() != OrdersStatus.IN_PROGRESS &&
	        order.getStatus() != OrdersStatus.ACCEPTED_PENDING_PAYMENT) {
	        throw new InvalidOrderException("Order not ready for settlement");
	    }

	    Wallet platformWallet = walletService.getPlatformWallet();
	    Wallet artisanWallet = walletService.getWallet(
	            order.getService().getArtisan().getUser().getPublicID()
	    );

	    BigDecimal totalAmount = order.getTotalPriceSnapshot();

	    // ✅ توزيع مباشر
	    walletService.distributeAmount(
	            platformWallet,
	            artisanWallet,
	            totalAmount,
	            order.getOrderPublicID()
	    );

	    order.setStatus(OrdersStatus.COMPLETED);
	    order.setPaymentStatus(PaymentStatus.PAID);

	    orderRepository.save(order);
	}
}
	
	
	
	
	
	

