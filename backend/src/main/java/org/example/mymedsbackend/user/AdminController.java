package org.example.mymedsbackend.user;

import org.example.mymedsbackend.security.SecurityLog;
import org.example.mymedsbackend.security.SecurityLogRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final SecurityLogRepo securityLogRepo;

    public AdminController(
            SecurityLogRepo securityLogRepo) {
        this.securityLogRepo = securityLogRepo;

    }

    @GetMapping("/security-logs")
    public List<Map<String, Object>> securityLogs() {
        return securityLogRepo.findTop200ByOrderByEventTimeDesc().stream().map(this::row).toList();
    }

    private Map<String, Object> row(SecurityLog log) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", log.getId());
        row.put("time", log.getEventTime());
        row.put("event", log.getEventName());
        row.put("user", log.getUsername());
        row.put("type", log.getEventType());
        row.put("result", log.getResult());
        row.put("details", log.getDetails());
        return row;
    }

}