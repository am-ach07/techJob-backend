package com.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(example = "true")
    private boolean success;

    @Schema(example = "Operation completed successfully")
    private String message;

    @Schema(description = "List of validation or business errors")
    private List<String> errors;

    @Schema(description = "Response payload")
    private T data;
    @Schema(description = "Timestamp of the response", example = "2024-06-01T12:34:56")
    private LocalDateTime timestamp;

	// getters & setters
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ApiResponse(boolean success, String message, List<String> errors, T data) {
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }


	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
    
}
