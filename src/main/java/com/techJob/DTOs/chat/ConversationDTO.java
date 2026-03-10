package com.techJob.DTOs.chat;

import java.time.LocalDateTime;
import java.util.List;

public class ConversationDTO {

	
	
	private String conversationPublicID;

    private List<ConversationParticipantDTO> participants ;

    

    private LocalDateTime createdAt ;

    private LocalDateTime updatedAt;
    
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
    private Boolean unlocked;
    
    
    
    
	public String getConversationPublicID() {
		return conversationPublicID;
	}
	public void setConversationPublicID(String conversationPublicID) {
		this.conversationPublicID = conversationPublicID;
	}
	public List<ConversationParticipantDTO> getParticipants() {
		return participants;
	}
	public void setParticipants(List<ConversationParticipantDTO> participants) {
		this.participants = participants;
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
	public Boolean getUnlocked() {
		return unlocked;
	}
	public void setUnlocked(Boolean unlocked) {
		this.unlocked = unlocked;
	}
    
    
    
}
