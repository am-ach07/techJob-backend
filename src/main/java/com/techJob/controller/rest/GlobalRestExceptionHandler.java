package com.techJob.controller.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.techJob.exception.InvalidActionException;
import com.techJob.exception.ResourceNotFoundException;
import com.techJob.exception.artisan.SkillCannotBeNull;
import com.techJob.exception.auth.EmailAlreadyExistsException;
import com.techJob.exception.auth.InvalidLoginCredentialsException;
import com.techJob.exception.auth.PhoneNumberAlreadyExistsException;
import com.techJob.exception.auth.RefreshTokenException;
import com.techJob.exception.auth.TokenExpiredException;
import com.techJob.exception.auth.TokenInvalidException;
import com.techJob.exception.auth.UsernameAlreadyExistsException;
import com.techJob.exception.emailVerification.AccountNotVerifiedException;
import com.techJob.exception.emailVerification.EmailAlreadyVerifiedException;
import com.techJob.exception.emailVerification.EmailSendingException;
import com.techJob.exception.emailVerification.EmailVerificationTokenException;
import com.techJob.exception.image.ImageException;
import com.techJob.exception.notifications.NotificationsNotFoundException;
import com.techJob.exception.offer.InvalidOfferException;
import com.techJob.exception.offer.OfferNotFoundException;
import com.techJob.exception.order.InvalidOrderException;
import com.techJob.exception.order.OrderNotFoundException;
import com.techJob.response.ApiResponse;
import com.techJob.response.ApiResponseFactory;

@RestControllerAdvice
public class GlobalRestExceptionHandler {
	
	
	

    @ExceptionHandler(NotificationsNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotificationsNotFound(NotificationsNotFoundException ex) {

    	return ApiResponseFactory.error(
    	        "Resource not found",
    	        List.of(ex.getMessage()),
    	        HttpStatus.NOT_FOUND
    	    );
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

    	List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        return ApiResponseFactory.error(
            "Validation failed",
            errors,
            HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailExists(EmailAlreadyExistsException ex) {
    	return ApiResponseFactory.error(
    	        "Email  already exists",
    	        List.of(ex.getMessage()),
    	        HttpStatus.CONFLICT
    	    );

}
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameExists(
    		UsernameAlreadyExistsException ex) {
    	return ApiResponseFactory.error(
    	        "Username  already exists",
    	        List.of(ex.getMessage()),
    	        HttpStatus.CONFLICT
    	    );

}
    
    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handlePhoneNumberExists(
    		PhoneNumberAlreadyExistsException ex) {
    	return ApiResponseFactory.error(
    	        "Phone Number  already exists",
    	        List.of(ex.getMessage()),
    	        HttpStatus.CONFLICT
    	    );

}
    
    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleRefreshToken(
            RefreshTokenException ex) {

        return ApiResponseFactory.error(
                "REFRESH_TOKEN_ERROR",
                List.of(ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }
    
    @ExceptionHandler(InvalidLoginCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidLogin(InvalidLoginCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(
                        false,
                        "Authentication failed",
                        List.of(ex.getMessage()),
                        null
                ));
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
			ResourceNotFoundException ex) {

		return ApiResponseFactory.error(
				"RESOURCE_NOT_FOUND",
				List.of(ex.getMessage()),
				HttpStatus.NOT_FOUND
		);
	}
    
    
    
    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenInvalidException(
            TokenInvalidException ex) {

        return ApiResponseFactory.error(
                "REFRESH_TOKEN_ERROR",
                List.of(ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenExpiredException(
            TokenExpiredException ex) {

        return ApiResponseFactory.error(
                "TOKEN_EXPIRED",
                List.of(ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }
    @ExceptionHandler(InvalidOfferException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidOfferException(
            InvalidOfferException ex) {

        return ApiResponseFactory.error(
                "Authentication failed",
                List.of(ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }
    
    @ExceptionHandler(SkillCannotBeNull.class)
    public ResponseEntity<ApiResponse<Object>> handleSkillCannotBeNull(
    		SkillCannotBeNull ex) {
        return ApiResponseFactory.error(
                "SKILL_CANNOT_BE_NULL",
                List.of(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidOrderException(
    		InvalidOrderException ex) {
        return ApiResponseFactory.error(
                "ORDER_EXCEPTION",
                List.of(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleOfferNotFound(OfferNotFoundException ex) {

    	return ApiResponseFactory.error(
    	        "Offer not found",
    	        List.of(ex.getMessage()),
    	        HttpStatus.NOT_FOUND
    	    );
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleOrderNotFound(OrderNotFoundException ex) {
    	return ApiResponseFactory.error(
		        "Order not found",
		        List.of(ex.getMessage()),
		        HttpStatus.NOT_FOUND
		    );
    }
    
    @ExceptionHandler(ImageException.class)
    public ResponseEntity<ApiResponse<Object>> handleImageException(ImageException ex) {
    	return ApiResponseFactory.error(
		        "Image error",
		        List.of(ex.getMessage()),
		        HttpStatus.BAD_REQUEST
		    );
    }
    
    
    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    ResponseEntity<ApiResponse<Object>> handleEmailAlreadyVerified(EmailAlreadyVerifiedException ex) {
		return ApiResponseFactory.error(
		        "Email already verified",
		        List.of(ex.getMessage()),
		        HttpStatus.BAD_REQUEST
		    );
	}
    @ExceptionHandler(InvalidActionException.class)
    ResponseEntity<ApiResponse<Object>> handleInvalidActionException(InvalidActionException ex) {
		return ApiResponseFactory.error(
		        "Invalid Action Exception",
		        List.of(ex.getMessage()),
		        HttpStatus.BAD_REQUEST
		    );
	}
    @ExceptionHandler(EmailSendingException.class)
    ResponseEntity<ApiResponse<Object>> handleEmailSendingException(EmailSendingException ex) {
    	return ApiResponseFactory.error(
		        "Email sending error",
		        List.of(ex.getMessage()),
		        HttpStatus.BAD_REQUEST
		    );
    }
    @ExceptionHandler(EmailVerificationTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailVerificationTokenException(EmailVerificationTokenException ex) {
		return ApiResponseFactory.error(
		        "Email verification token error",
		        List.of(ex.getMessage()),
		        HttpStatus.BAD_REQUEST
		    );
	}
    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccountNotVerifiedException(AccountNotVerifiedException ex)
    {
    	return ApiResponseFactory.error(
		        "Account not verified",
		        List.of(ex.getMessage()),
		        HttpStatus.UNAUTHORIZED
		    );
    }
    
}
    



