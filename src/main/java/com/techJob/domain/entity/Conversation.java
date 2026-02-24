package com.techJob.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String conversationPublicID;

    @OneToMany(mappedBy = "conversation",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<ConversationParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "conversation",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getConversationPublicID() {
		return conversationPublicID;
	}
	public void setConversationPublicID(String conversationPublicID) {
		this.conversationPublicID = conversationPublicID;
	}
	
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public List<ConversationParticipant> getParticipants() {
		return participants;
	}
	public void setParticipants(List<ConversationParticipant> participants) {
		this.participants = participants;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	
	
	
	
}
