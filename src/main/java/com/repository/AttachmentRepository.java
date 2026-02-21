package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.domain.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

}
