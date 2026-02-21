package com.exception.auth;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.response.ApiResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper ;
    
    

    public JwtAuthEntryPoint(ObjectMapper mapper) {
		super();
		this.mapper = mapper;
	}



	@Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        String errorMsg = "Unauthorized request";
        
        if (authException instanceof InvalidLoginCredentialsException) {
            errorMsg = authException.getMessage();
        }
        ApiResponse<Object> body = new ApiResponse<>();
        body.setSuccess(false);
        body.setMessage("Authentication failed: " + errorMsg);
        body.setErrors(List.of(errorMsg));
        body.setData(null);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        mapper.writeValue(response.getOutputStream(), body);
    }
}
