package com.techJob.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;




@Entity
@Table(
	    name = "messages",
	    indexes = {
	        @Index(name = "idx_conversation_created", columnList = "conversation_id, createdAt")
	    }
	)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String messagePublicID;

    @Column(nullable = false, length = 2000)
    private String content;

    

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime readAt;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getMessagePublicID() {
		return messagePublicID;
	}


	public void setMessagePublicID(String messagePublicID) {
		this.messagePublicID = messagePublicID;
	}


	


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public LocalDateTime getReadAt() {
		return readAt;
	}


	public void setReadAt(LocalDateTime readAt) {
		this.readAt = readAt;
	}


	public Conversation getConversation() {
		return conversation;
	}


	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}


	public User getSender() {
		return sender;
	}


	public void setSender(User sender) {
		this.sender = sender;
	}

	
	
	
}
