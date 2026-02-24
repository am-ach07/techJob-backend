package com.techJob.domain.entity;

import java.time.LocalDateTime;

import com.techJob.domain.enums.Roles;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "conversation_participants",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"conversation_id", "user_id"})
    }
)
public class ConversationParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime joinedAt = LocalDateTime.now();

    private LocalDateTime lastReadAt;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Conversation getConversation() {
		return conversation;
	}
	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
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
    
    
    
    
}