package com.techJob.domain.entity;

import java.time.LocalDateTime;

import com.techJob.domain.enums.MessageStatus;
import com.techJob.domain.enums.MessageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;




@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String messagePublicID;

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus;

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


	public MessageType getMessageType() {
		return messageType;
	}


	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}


	public MessageStatus getMessageStatus() {
		return messageStatus;
	}


	public void setMessageStatus(MessageStatus messageStatus) {
		this.messageStatus = messageStatus;
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
