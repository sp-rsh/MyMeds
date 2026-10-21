package org.example.mymedsbackend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final SecurityLogrepo securityLogRepo;
    public AdminController(
            SecurityLogrepo securityLogRepo) {
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