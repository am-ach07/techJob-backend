package com.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.domain.entity.EmailVerificationToken;
import com.domain.entity.User;
import com.exception.emailVerification.EmailAlreadyVerifiedException;
import com.exception.emailVerification.EmailSendingException;
import com.exception.emailVerification.EmailVerificationTokenException;
import com.repository.EmailVerificationTokenRepository;

@Service
public class EmailVerificationService{
	
	
	private final EmailVerificationTokenRepository tokenRepository;

	public EmailVerificationService(EmailVerificationTokenRepository tokenRepository) {
		super();
		this.tokenRepository = tokenRepository;
	}
	
	
	public EmailVerificationToken createOrUpdateToken(User user) {
		EmailVerificationToken token =
	            tokenRepository.findByUser(user)
	                    .orElse(new EmailVerificationToken());
		String tokenStr = java.util.UUID.randomUUID().toString();
		token.setToken(tokenStr);
		token.setUser(user);
		token.setExpiryDate(java.time.LocalDateTime.now().plusHours(24));
		return tokenRepository.save(token);
	}
	
	public EmailVerificationToken findByToken(String token) {
	    return tokenRepository.findByToken(token)
	            .orElseThrow(() -> new EmailVerificationTokenException("Invalid token"));
	}
	@Transactional
	public void deleteToken(EmailVerificationToken token) {
	    tokenRepository.delete(token);
	}
	@Transactional
	public void deleteByUser(User user) {
	    tokenRepository.deleteByUser(user);
	}
	
	public void verifyLastToken(User user) {
		EmailVerificationToken lastToken =
		        tokenRepository.findLatestByUser(user);

		 
		
		if (lastToken != null &&
		    lastToken.getCreatedAt()
		            .isAfter(LocalDateTime.now().minusMinutes(2))) {

		    throw new EmailSendingException("Wait before requesting again");
		}

	}
	
	
}