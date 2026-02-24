package com.techJob.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_audit_logs")
public class AuthAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;           // publicID المستخدم
    private String action;           // مثال: LOGIN_SUCCESS, LOGIN_FAILED
    private String ip;               // IP المستخدم
    private String userAgent;        // User-Agent
    private LocalDateTime timestamp; // وقت الحدث
    private String status;           // SUCCESS / FAILED / BLOCKED

    // Constructors
    public AuthAuditLog() {}

    public AuthAuditLog(String userId, String action, String ip, String userAgent, String status) {
        this.userId = userId;
        this.action = action;
        this.ip = ip;
        this.userAgent = userAgent;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }

    // Getters and Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
}
