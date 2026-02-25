package com.techJob.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techJob.DTOs.auth.LoginRequestDTO;
import com.techJob.DTOs.auth.RefreshTokenRequestDTO;
import com.techJob.DTOs.auth.RegisterRequestDTO;
import com.techJob.DTOs.user.EmailRequest;
import com.techJob.domain.entity.Address;
import com.techJob.domain.entity.ArtisanProfile;
import com.techJob.domain.entity.EmailVerificationToken;
import com.techJob.domain.entity.RefreshToken;
import com.techJob.domain.entity.User;
import com.techJob.domain.enums.Roles;
import com.techJob.exception.auth.EmailAlreadyExistsException;
import com.techJob.exception.auth.InvalidLoginCredentialsException;
import com.techJob.exception.auth.RefreshTokenException;
import com.techJob.exception.auth.UsernameAlreadyExistsException;
import com.techJob.exception.emailVerification.EmailSendingException;
import com.techJob.exception.emailVerification.EmailVerificationTokenException;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.repository.UserRepository;
import com.techJob.response.JWTResponseDTO;
import com.techJob.security.constants.SecurityConstants;
import com.techJob.security.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthenticationService {

	
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    
  
	
	
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final NotificationsServiceImp notificationService;
    private final AuthAuditLogService auditLogService;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 EmailVerificationService emailVerificationService,
                                 EmailService emailService,
                                 AuthenticationManager authenticationManager,
                                 JwtService jwtService,
                                 RefreshTokenService refreshTokenService,
                                 NotificationsServiceImp notificationService,
                                 AuthAuditLogService auditLogService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }


  //get current user
  	private User getCurrentUser() {
  		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
  	    String email = auth.getName();

  	    return userRepository
  	            .findByUsernameOrEmail(email, email)
  	            .orElseThrow(() -> new UserNotFoundException(email));
  	}
    
    // ============================
    // REGISTER
    // ============================

    @Transactional
    public void register(RegisterRequestDTO request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        if (request.getRole() == Roles.ADMIN) {
            throw new InvalidLoginCredentialsException("Cannot self-register as ADMIN");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPublicID(UUID.randomUUID().toString()); // Use publicID for all tokens
        user.setRole(request.getRole());
        


        

        if(request.getRole().equals(Roles.ARTISAN)) {
            ArtisanProfile profile = new ArtisanProfile();
            profile.setUser(user);
            user.setArtisanProfile(profile);
        }

        userRepository.save(user);

        try {
        EmailVerificationToken token =
                emailVerificationService.createOrUpdateToken(user);

        emailService.sendVerificationEmail(user, token);

        notificationService.createNotification(
                user.getPublicID(),
                "Welcome!",
                "Thank you for joining " + user.getUsername()
        );
        } catch (EmailSendingException ex) {
			logger.error("Failed to send verification email to {}: {}", user.getEmail(), ex.getMessage());
        }
}
	 	// ============================
	    // RESEND EMAIL VERIFICATION
	    // ============================
    @Transactional
    public void resendVerification(EmailRequest request) {
		String email = request.getEmail();

    	logger.info("Resend verification requested for email: {}", email);
        User user=userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EmailSendingException("verification link was sent"));
        
        emailVerificationService.verifyLastToken(user);
        
        if (user.getEmailVerified()) {
            throw new EmailSendingException("Email already verified");
        }

        // Delete old tokens
        emailVerificationService.deleteByUser(user);

        // Create new token
        EmailVerificationToken newToken =
                emailVerificationService.createOrUpdateToken(user);

        // Send email
        emailService.sendVerificationEmail(user, newToken);
    }

    
    
    
    
    // ============================
    // LOGIN
    // ============================

    public JWTResponseDTO login(LoginRequestDTO requestDTO, HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader(SecurityConstants.USER_AGENT_HEADER);

        String userId = null;

        // نحاول جلب المستخدم قبل المصادقة
        User user = userRepository
                .findByUsernameOrEmail(
                        requestDTO.getUsernameOrEmail(),
                        requestDTO.getUsernameOrEmail())
                .orElse(null);
        if (user != null) {
            userId = user.getPublicID();
        }

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDTO.getUsernameOrEmail(),
                            requestDTO.getPassword()
                    )
            );

            if (user == null) {
                throw new InvalidLoginCredentialsException("Invalid username/email or password");
            }

            String accessToken = jwtService.generateAccessToken(user);

            refreshTokenService.deleteByUser(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            Date accessExpiration = jwtService.extractExpiration(accessToken);

            notificationService.createNotification(
                    user.getPublicID(),
                    "Login Alert",
                    "New login detected"
            );


            return new JWTResponseDTO(
                    accessToken,
                    refreshToken.getToken(),
                    accessExpiration.getTime()
            );

        } catch (AuthenticationException ex) {

            // ✅ FAILED LOG (يشمل InternalAuthenticationServiceException)
            auditLogService.log(
                    userId,
                    SecurityConstants.LOGIN_ACTION,
                    ip,
                    userAgent,
                    SecurityConstants.FAILED_ACTION
            );

            throw new InvalidLoginCredentialsException("Invalid username/email or password");
        }
    }


    // ============================
    // REFRESH TOKEN
    // ============================

    public JWTResponseDTO refreshToken(RefreshTokenRequestDTO request) {

        RefreshToken oldToken =
                refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        User user = oldToken.getUser();

        // ROTATE REFRESH TOKEN
        refreshTokenService.deleteByUser(user);
        RefreshToken newRefresh = refreshTokenService.createRefreshToken(user);

        String newAccessToken = jwtService.generateAccessToken(user);
        Date expiration = jwtService.extractExpiration(newAccessToken);

        user.setTokenVersion(user.getTokenVersion() + 1);
        
        return new JWTResponseDTO(
                newAccessToken,
                newRefresh.getToken(),
                expiration.getTime()
        );
    }
 // ============================
    // verfiy email
    // ============================

    @Transactional
    public void verifyEmail(String token) {

        EmailVerificationToken verificationToken =
                emailVerificationService.findByToken(token);

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new EmailVerificationTokenException("Token expired");
        }

        User user = verificationToken.getUser();

        user.setEmailVerified(true);
        userRepository.save(user);

        emailVerificationService.deleteToken(verificationToken);
    }


    // ============================
    // LOGOUT
    // ============================

    @Transactional
    public void logout() {

    	User user=getCurrentUser();
    	
        try {

        	
        			
            refreshTokenService.deleteByUser(user);
            logger.debug("User {} delete refresh Token  ",user);
            user.setTokenVersion(user.getTokenVersion() + 1);
            
            userRepository.save(user);

        } catch (RefreshTokenException ignored) {
            
        }
    }
}

