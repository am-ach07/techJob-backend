package com.techJob.DTOs.chat;

import java.time.LocalDateTime;

import com.techJob.DTOs.user.UserDTO;

public class ConversationParticipantDTO {

	private UserDTO user;

    private LocalDateTime joinedAt ;

    private LocalDateTime lastReadAt;
    private int unreadCount;
    
    
    
    
	public UserDTO getUser() {
		return user;
	}
	public void setUser(UserDTO user) {
		this.user = user;
	}
	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}
	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}
	public LocalDateTime getLastReadAt() {
		return lastReadAt;
	}
	public void setLastReadAt(LocalDateTime lastReadAt) {
		this.lastReadAt = lastReadAt;
	}
	public int getUnreadCount() {
		return unreadCount;
	}
	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}
    
    
    
}
