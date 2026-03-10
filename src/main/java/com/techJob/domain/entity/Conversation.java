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

    

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
    
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
    private Boolean unlocked;

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
	
	public String getLastMessagePreview() {
		return lastMessagePreview;
	}
	public void setLastMessagePreview(String lastMessagePreview) {
		this.lastMessagePreview = lastMessagePreview;
	}
	public LocalDateTime getLastMessageAt() {
		return lastMessageAt;
	}
	public void setLastMessageAt(LocalDateTime lastMessageAt) {
		this.lastMessageAt = lastMessageAt;
	}
	public Boolean isUnlocked() {
		return unlocked;
	}
	public void setUnlocked(Boolean unlocked) {
		this.unlocked = unlocked;
	}
	
	
	
	
	
}
