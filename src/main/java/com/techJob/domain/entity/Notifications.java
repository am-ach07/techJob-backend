package com.techJob.domain.entity;

import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;


	@Entity
	@Table(
	    name = "notifications",
	    indexes = {
	        @Index(name = "idx_notifications_user", columnList = "user_id"),
	        @Index(name = "idx_notifications_read", columnList = "is_read")
	    }
	)
	 public class Notifications {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user;

	    @Column(nullable = false, length = 255)
	    private String title;

	    @Column(nullable = false, length = 1000)
	    private String message;

	    @Column(name = "is_read", nullable = false)
	    private boolean isRead = false;

	    @Column(nullable = false, updatable = false)
	    private Instant createdAt;

	    @PrePersist
	    protected void onCreate() {
	        this.createdAt = Instant.now();
	    }
	    
	    // Getters and Setters


		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public boolean isRead() {
			return isRead;
		}

		public void setRead(boolean isRead) {
			this.isRead = isRead;
		}

		public Instant getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Instant createdAt) {
			this.createdAt = createdAt;
		}
	    
	    
	    
	}


