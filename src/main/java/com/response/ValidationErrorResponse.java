package com.response;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorResponse {

    private int status;
    private Map<String, String> errors;
    private LocalDateTime timestamp;

    public ValidationErrorResponse(int status, Map<String, String> errors) {
        this.status = status;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
