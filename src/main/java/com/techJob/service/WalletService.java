package com.techJob.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.techJob.domain.entity.User;
import com.techJob.domain.entity.Wallet;
import com.techJob.domain.entity.WalletTransaction;
import com.techJob.domain.enums.TransactionType;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.repository.UserRepository;
import com.techJob.repository.WalletRepositrory;
import com.techJob.repository.WalletTransactionRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WalletService {

	
	
	private final WalletTransactionRepository transactionRepository;

	private final UserRepository userRepository;

	private final WalletRepositrory walletRepositrory;

	@Value("${system.public.id}")
	private String systemPublicID;
	

	
	
	public WalletService(
			WalletTransactionRepository transactionRepository,
			WalletRepositrory walletRepositrory,
			UserRepository userRepository) {
		super();
		this.transactionRepository = transactionRepository;
		this.userRepository = userRepository;
		this.walletRepositrory = walletRepositrory;
	}
	
	private void recordTransaction(
	        Wallet from,
	        Wallet to,
	        BigDecimal amount,
	        TransactionType type,
	        String referenceId
	) {

		if(amount.compareTo(BigDecimal.ZERO) <= 0) return;
		
	    WalletTransaction tx = new WalletTransaction();
	    tx.setFromWallet(from);
	    tx.setToWallet(to);
	    tx.setAmount(amount);
	    tx.setType(type);
	    tx.setReferenceId(referenceId);
	    tx.setCreatedAt(LocalDateTime.now());

	    transactionRepository.save(tx);
	}
	
	public Wallet getPlatformWallet() {

		User user=userRepository.findByPublicID(systemPublicID).
				orElseThrow(() -> new UserNotFoundException("Platform user not found"));
		
		
		Wallet platformWallet = walletRepositrory.findByUser(user).
				orElseThrow(() -> new RuntimeException("Platform wallet not found"));
		
		return platformWallet;
	}
	
	public Wallet getWallet(String publicID) {
	   
		User user=userRepository.findByPublicID(publicID).
				orElseThrow(()->new UserNotFoundException("User not found with public ID: " + publicID));
		
		Wallet wallet=walletRepositrory.findByUser(user)
	            .orElseThrow(() -> new RuntimeException("Wallet not found for user with public ID: " + publicID));
		
	    return wallet;
	}
	

	//===============================frozen amount in pending balance until order completion====================================
	@Transactional
	public void freezeToEscrow(Wallet platformWallet, BigDecimal amount,String orderId) {

	    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
	        throw new IllegalArgumentException("Invalid amount");
	    }

	    platformWallet.setPendingBalance(
	            platformWallet.getPendingBalance().add(amount)
	    );
	    
	    recordTransaction(
	            null,
	            platformWallet,
	            amount,
	            TransactionType.DEPOSIT_ESCROW,
	            orderId
	    );
	    
	    walletRepositrory.save(platformWallet);
	}
	//===============================release frozen amount from pending balance ====================================
	@Transactional
	public void releaseFromEscrow(Wallet platformWallet, BigDecimal totalAmount,String orderId) {

	    if (platformWallet.getPendingBalance().compareTo(totalAmount) < 0) {
	        throw new IllegalStateException("Insufficient escrow balance");
	    }

	    
	    
	    
	    recordTransaction(
	            platformWallet,
	            null,
	            totalAmount,
	            TransactionType.FINAL_ESCROW,
	            orderId
	    );
	    
	    walletRepositrory.save(platformWallet);
	}
	//===============================release order amount to artisan and platform====================================
	@Transactional
	public void distributeAmount(
	        Wallet platformWallet,
	        Wallet artisanWallet,
	        BigDecimal totalAmount,
	        String orderId
	) {

	    BigDecimal commission = totalAmount.multiply(BigDecimal.valueOf(0.05));
	    BigDecimal artisanNet = totalAmount.subtract(commission);

	    // 1️⃣ إزالة من pending
	    platformWallet.setPendingBalance(
	        platformWallet.getPendingBalance().subtract(totalAmount)
	    );

	    
	    
	    
	    // 2️⃣ إضافة صافي للحرفي
	    artisanWallet.setBalance(
	        artisanWallet.getBalance().add(artisanNet)
	    );

	    recordTransaction(
	    		null,
	    		platformWallet,
	    		commission,
	    		TransactionType.PLATFORM_COMMISSION,
	    		orderId
	    		);
	    
	    // 3️⃣ إضافة العمولة لرصيد المنصة
	    platformWallet.setBalance(
	        platformWallet.getBalance().add(commission)
	    );
	    
	    recordTransaction(
	            platformWallet,
	            artisanWallet,
	            artisanNet,
	            TransactionType.RELEASE_TO_ARTISAN,
	            orderId
	    );
	    
	    
	    walletRepositrory.save(artisanWallet);
	    walletRepositrory.save(platformWallet);
	}
	
	//===============================refund amount to client from escrow====================================
	@Transactional
	public void refundToClient(
	        Wallet platformWallet,
	        Wallet clientWallet,
	        BigDecimal amount,
	        String orderId
	) {

	    // 1️⃣ التأكد أن المال موجود في escrow
	    if (platformWallet.getPendingBalance().compareTo(amount) < 0) {
	        throw new IllegalStateException("Insufficient escrow balance");
	    }

	    // 2️⃣ سحب من المبلغ المجمد
	    platformWallet.setPendingBalance(
	            platformWallet.getPendingBalance().subtract(amount)
	    );

	    // 3️⃣ إرجاع المال للعميل
	    clientWallet.setBalance(
	            clientWallet.getBalance().add(amount)
	    );

	    // 4️⃣ تسجيل العملية
	    recordTransaction(
	            platformWallet,
	            clientWallet,
	            amount,
	            TransactionType.REFUND_TO_CLIENT,
	            orderId
	    );
	}
	
	//===============================resolve dispute by refunding client====================================
	@Transactional
	public void resolveDisputeRefund(
	        Wallet platformWallet,
	        Wallet clientWallet,
	        BigDecimal amount,
	        String orderId
	) {

	    refundToClient(platformWallet, clientWallet, amount, orderId);
	}
	
	
}
