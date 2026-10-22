package org.example.mymedsbackend.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
public class SecurityLogService {

    private final SecurityLogRepo repo;
    public SecurityLogService(SecurityLogRepo repo) {
        this.repo = repo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String eventName, String username, String type, String result, String details) {
        try {
            SecurityLog log = new SecurityLog();
            log.setEventTime(LocalDateTime.now());
            log.setEventName(eventName);
            log.setUsername(username == null || username.isBlank() ? "Unknown" : username);
            log.setEventType(type);
            log.setResult(result);
            log.setDetails(details);
            repo.save(log);
        } catch (Exception ignored) {
        }
    }

    public void recordAsCurrentUser(String eventName, String type, String result, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth == null ? null : auth.getName();
        if ("anonymousUser".equals(name)) name = null;
        record(eventName, name, type, result, details);
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        record("Failed login", event.getAuthentication().getName(), "Login", "Failed",
                event.getException().getMessage());
    }
}