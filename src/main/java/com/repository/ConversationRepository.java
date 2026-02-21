package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.domain.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
