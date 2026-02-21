package com.response;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseFactory {

    public static <T> ResponseEntity<ApiResponse<T>> ok(
            T data, String message) {

        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<ApiResponse<Object>> error(
            String message, List<String> errors, HttpStatus status) {

        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setErrors(errors);

        return ResponseEntity.status(status).body(response);
    }
    
    
    public static <T> ResponseEntity<ApiResponse<T>> tooManyRequests(String message) {

        ApiResponse<T> response = new ApiResponse<>();

        response.setSuccess(false);
        response.setMessage(message);
        response.setData(null);

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(response);
    }
}
