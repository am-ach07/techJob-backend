package com.techJob.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.techJob.domain.entity.Conversation;
import com.techJob.domain.entity.User;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

	
	
	
	

	Optional<Conversation> findByConversationPublicID(String conversationPublicID);

	// يعيد المحادثات التي يكون فيها المستخدم مشاركًا
	Page<Conversation> findAllByParticipantsUser(User user, Pageable pageable);
	
	
}