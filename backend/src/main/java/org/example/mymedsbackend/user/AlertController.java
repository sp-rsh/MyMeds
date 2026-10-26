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
    private final UserRepo userRepo;

    public AlertController(MedicationRepo medicationRepo, MedicationLogRepo logRepo, UserRepo userRepo) {
        this.medicationRepo = medicationRepo;
        this.logRepo = logRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAlerts(
            @AuthenticationPrincipal UserDetails user) {

        String username = user.getUsername();
        User patient = userRepo.findByUsername(username);
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }
        List<Medication> medications = medicationRepo.findByPatient_Id(patient.getId());
        List<Map<String, Object>> alerts = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        List<MedicationLog> logs =
                logRepo.findByPatient_IdAndEventTimeBetweenOrderByEventTimeDesc(
                        patient.getId(),
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
                if (log.getMedication().getId() == medication.getId()) {
                    dispensed = true;
                    break;
                }
            }

            if (!dispensed) {
                Map<String, Object> alert = new HashMap<>();
                alert.put("medicationId", medication.getId());
                alert.put("medicationName", medication.getName());
                alert.put("scheduledTime", scheduledTime);
                alert.put("message", "Scheduled medication dose has been missed");
                alerts.add(alert);
            }
        }

        return ResponseEntity.ok(alerts);
    }
}
