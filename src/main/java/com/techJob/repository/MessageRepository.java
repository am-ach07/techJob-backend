package com.techJob.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.Conversation;
import com.techJob.domain.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

	
	Page<Message> findByConversation_IdOrderByCreatedAtDesc(
	        Conversation conversation,
	        Pageable pageable
	);

	Page<Message> findAllByConversation(Conversation conversation, Pageable pageable);
	
	
}
