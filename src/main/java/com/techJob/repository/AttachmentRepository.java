package com.techJob.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

}
