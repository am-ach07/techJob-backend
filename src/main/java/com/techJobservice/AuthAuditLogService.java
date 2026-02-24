package com.techJobservice;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.techJob.domain.entity.AuthAuditLog;
import com.techJob.repository.AuthAuditLogRepository;

@Service
public class AuthAuditLogService {

    private final AuthAuditLogRepository auditLogRepository;

    public AuthAuditLogService(AuthAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    public void log(String userId, String action, String ip, String userAgent, String status) {
        AuthAuditLog log = new AuthAuditLog(userId, action, ip, userAgent, status);
        auditLogRepository.save(log);
    }
}
