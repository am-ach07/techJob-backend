package com.techJob.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
