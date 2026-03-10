package com.techJob.DTOs.chat;

import java.time.LocalDateTime;

import com.techJob.DTOs.user.UserDTO;

public class MessageDTO {

	private String messagePublicID;

	private String content;

	private ConversationDTO conversation;

	private UserDTO sender;

	private LocalDateTime createdAt;

	private LocalDateTime readAt;

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

	public ConversationDTO getConversation() {
		return conversation;
	}

	public void setConversation(ConversationDTO conversation) {
		this.conversation = conversation;
	}

	public UserDTO getSender() {
		return sender;
	}

	public void setSender(UserDTO sender) {
		this.sender = sender;
	}
	
	

}
