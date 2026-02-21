package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.domain.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
