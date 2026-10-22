package org.example.mymedsbackend.user;

import org.example.mymedsbackend.medication.Medication;
import org.example.mymedsbackend.medication.MedicationLog;
import org.example.mymedsbackend.medication.MedicationLogRepo;
import org.example.mymedsbackend.medication.MedicationRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final MedicationRepo medicationRepo;
    private final MedicationLogRepo logRepo;

    public AlertController(MedicationRepo medicationRepo, MedicationLogRepo logRepo) {
        this.medicationRepo = medicationRepo;
        this.logRepo = logRepo;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAlerts(
            @AuthenticationPrincipal UserDetails user) {

        String username = user.getUsername();
        List<Medication> medications = medicationRepo.findByPatientUsername(username);
        List<Map<String, Object>> alerts = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        List<MedicationLog> logs =
                logRepo.findByPatientUsernameAndEventTypeAndEventTimeBetween(
                        username,
                        "DISPENSED",
                        today.atStartOfDay(),
                        LocalDateTime.now()
                );

        for (Medication medication : medications) {
            LocalTime scheduledTime = medication.getScheduledTime();

            if (scheduledTime == null || !currentTime.isAfter(scheduledTime)) {
                continue;
            }

            boolean dispensed = false;

            for (MedicationLog log : logs) {
                if (log.getMedicationId() == medication.getId()) {
                    dispensed = true;
                    break;
                }
            }

            if (!dispensed) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("medicationId", medication.getId());
                alert.put("medicationName", medication.getMedicationName());
                alert.put("scheduledTime", scheduledTime);
                alert.put("message", "Scheduled medication dose has been missed");
                alerts.add(alert);
            }
        }

        return ResponseEntity.ok(alerts);
    }
}
