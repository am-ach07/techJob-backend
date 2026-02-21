package com.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.DTOs.auth.LoginRequestDTO;
import com.DTOs.auth.RefreshTokenRequestDTO;
import com.DTOs.auth.RegisterRequestDTO;
import com.DTOs.user.EmailRequest;
import com.response.ApiResponse;
import com.response.ApiResponseFactory;
import com.response.JWTResponseDTO;
import com.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {
	
	
    private static final Logger logger = LoggerFactory.getLogger(AuthRestController.class);

	
	
    private final AuthenticationService authenticationService;

    public AuthRestController(
    		AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    
    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details.")
    @ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
	})
    // تسجيل مستخدم جديد
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
    	
        authenticationService.register(registerRequestDTO);
        
        return ResponseEntity.ok("User registered successfully");
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token upon successful login.")
    @ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    					
    })
    // تسجيل الدخول
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JWTResponseDTO>> login(
    		@Valid @RequestBody LoginRequestDTO loginRequestDTO,
    		HttpServletRequest request) {
    	
    	

    	

    	
    	
        JWTResponseDTO jwtResponse = authenticationService.login(loginRequestDTO,request);
        return ApiResponseFactory.ok(jwtResponse,"Login successful");
    }

    @Operation(summary = "Refresh JWT token", description = "Generates a new JWT token using the provided refresh token.")
    @ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    // تجديد JWT باستخدام Refresh Token
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JWTResponseDTO>> refreshToken( @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
    	JWTResponseDTO jwtResponse = authenticationService.refreshToken(refreshTokenRequestDTO);
        return ApiResponseFactory.ok(jwtResponse,"Token refreshed successfully");
    }
    //verfiy email
    
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {

        authenticationService.verifyEmail(token);

        return ApiResponseFactory.ok(null, "Email verified successfully");
    }
    
    

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(
    		@RequestBody @Valid EmailRequest request,
    		HttpServletRequest httpServletRequest) {

		
		
        authenticationService.resendVerification(request);

        return ApiResponseFactory.ok(null, "Verification email resent");
    }

    
    
    @Operation(summary = "User logout", description = "Logs out the user by invalidating the provided refresh token.")
    @ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout successful"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid refresh token")
    })
    
    // تسجيل الخروج
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
    	
        authenticationService.logout();
        
        return ApiResponseFactory.ok(null, "Logout successful");
    }

}
