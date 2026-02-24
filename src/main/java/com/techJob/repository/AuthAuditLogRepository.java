package com.techJob.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techJob.domain.entity.AuthAuditLog;

public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, Long> {
    // يمكن لاحقًا إضافة طرق بحث حسب userId أو action
}
