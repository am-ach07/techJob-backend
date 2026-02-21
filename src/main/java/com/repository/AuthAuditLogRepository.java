package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.domain.entity.AuthAuditLog;

public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, Long> {
    // يمكن لاحقًا إضافة طرق بحث حسب userId أو action
}
