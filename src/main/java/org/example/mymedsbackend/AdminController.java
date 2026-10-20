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

    private final Databaserepo userRepo;
    private final Medicationrepo medicationRepo;
    private final MedicationLogrepo logRepo;
    private final SecurityLogrepo securityLogRepo;
    private final SystemSettingsrepo settingsRepo;
    private final SecurityLogService securityLog;

    public AdminController(Databaserepo userRepo,
                           Medicationrepo medicationRepo,
                           MedicationLogrepo logRepo,
                           SecurityLogrepo securityLogRepo,
                           SystemSettingsrepo settingsRepo,
                           SecurityLogService securityLog) {
        this.userRepo = userRepo;
        this.medicationRepo = medicationRepo;
        this.logRepo = logRepo;
        this.securityLogRepo = securityLogRepo;
        this.settingsRepo = settingsRepo;
        this.securityLog = securityLog;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        List<Credentials> users = userRepo.findAll();

        long doctors = users.stream().filter(u -> u.isActive() && "DOCTOR".equals(roleOf(u))).count();
        long patients = users.stream().filter(u -> u.isActive() && "PATIENT".equals(roleOf(u))).count();
        long admins = users.stream().filter(u -> u.isActive() && "ADMIN".equals(roleOf(u))).count();
        long inactive = users.stream().filter(u -> !u.isActive()).count();

        long dosesToday = logRepo
                .findByEventTimeAfterOrderByEventTimeDesc(LocalDate.now().atStartOfDay())
                .stream()
                .filter(l -> String.valueOf(l.getEventType()).toUpperCase(Locale.ROOT).contains("DISPENS"))
                .count();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("totalUsers", users.size());
        body.put("activeDoctors", doctors);
        body.put("activePatients", patients);
        body.put("activeAdmins", admins);
        body.put("inactiveUsers", inactive);
        body.put("totalMedications", medicationRepo.count());
        body.put("dosesToday", dosesToday);
        body.put("status", "Operational");
        body.put("recentActivity",
                securityLogRepo.findTop5ByOrderByEventTimeDesc().stream().map(this::row).toList());
        return body;
    }

    @GetMapping("/security-logs")
    public List<Map<String, Object>> securityLogs() {
        return securityLogRepo.findTop200ByOrderByEventTimeDesc().stream().map(this::row).toList();
    }

    @GetMapping("/settings")
    public SystemSettings getSettings() {
        return loadSettings();
    }

    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(@RequestBody SystemSettings incoming) {
        String name = incoming.getSystemName() == null ? "" : incoming.getSystemName().trim();
        if (name.isEmpty()) {
            return ResponseEntity.badRequest().body("System name is required.");
        }
        try {
            ZoneId.of(incoming.getTimezone());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Invalid timezone.");
        }
        incoming.setId(1);
        incoming.setSystemName(name);
        SystemSettings saved = settingsRepo.save(incoming);
        securityLog.recordAsCurrentUser("System settings updated", "Settings", "Success",
                "System settings were changed");
        return ResponseEntity.ok(saved);
    }

    private SystemSettings loadSettings() {
        return settingsRepo.findById(1).orElseGet(() -> settingsRepo.save(new SystemSettings()));
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

    private String roleOf(Credentials user) {
        String role = user.getRole() == null ? "" : user.getRole().trim().toUpperCase(Locale.ROOT);
        return role.startsWith("ROLE_") ? role.substring(5) : role;
    }
}