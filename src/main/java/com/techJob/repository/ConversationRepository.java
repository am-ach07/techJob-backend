package com.techJob.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}
