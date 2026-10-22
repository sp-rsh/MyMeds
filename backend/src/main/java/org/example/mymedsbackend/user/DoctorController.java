package org.example.mymedsbackend.user;

import org.example.mymedsbackend.medication.Medication;
import org.example.mymedsbackend.medication.MedicationLog;
import org.example.mymedsbackend.medication.MedicationLogRepo;
import org.example.mymedsbackend.medication.MedicationRepo;
import org.example.mymedsbackend.util.MedicationUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

    private final UserRepo userRepo;
    private final MedicationRepo medicationRepo;
    private final MedicationLogRepo logRepo;
    private final DoctorPatientAssignmentRepo assignmentRepo;

    public DoctorController(UserRepo userRepo, MedicationRepo medicationRepo, MedicationLogRepo logRepo, DoctorPatientAssignmentRepo assignmentRepo) {
        this.userRepo = userRepo;
        this.medicationRepo = medicationRepo;
        this.logRepo = logRepo;
        this.assignmentRepo = assignmentRepo;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails principal) {
        User doctor = currentDoctor(principal);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", doctor.getUsername());
        body.put("role", doctor.getRole());
        body.put("email", doctor.getEmail());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@AuthenticationPrincipal UserDetails principal) {
        User doctor = currentDoctor(principal);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<User> patients = patients(doctor.getId());
        List<MedicationLog> recentLogs = recentLogs();
        List<Map<String, Object>> activity = new ArrayList<>();
        int dosesToday = 0;
        int attentionNeeded = 0;

        for (User patient : patients) {
            List<Medication> meds = medicationRepo.findByPatientUsername(patient.getUsername());
            dosesToday += meds.size();
            if ("Attention".equals(patientStatus(patient, meds, recentLogs))) {
                attentionNeeded++;
            }
            for (Medication med : meds) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", med.getId());
                row.put("patient", patient.getUsername());
                row.put("medication", med.getMedicationName());
                row.put("time", formatTime(med.getScheduledTime()));
                row.put("status", medicationStatus(med, recentLogs, LocalDate.now()));
                activity.add(row);
            }
        }

        activity.sort(Comparator.comparing(row -> String.valueOf(row.get("time"))));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("doctorName", doctor.getUsername());
        body.put("totalPatients", patients.size());
        body.put("dosesToday", dosesToday);
        body.put("attentionNeeded", attentionNeeded);
        body.put("activity", activity);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/patients")
    public ResponseEntity<?> patients(@AuthenticationPrincipal UserDetails principal) {
        User doctor = currentDoctor(principal);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<MedicationLog> recentLogs = recentLogs();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (User patient : patients(doctor.getId())) {
            List<Medication> meds = medicationRepo.findByPatientUsername(patient.getUsername());
            Medication next = MedicationUtil.nextDose(meds);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", patient.getId());
            row.put("name", patient.getUsername());
            row.put("patientUsername", patient.getUsername());
            row.put("medication", next == null ? "" : next.getMedicationName());
            row.put("nextDose", next == null ? "" : formatTime(next.getScheduledTime()));
            row.put("adherence", adherence(meds, recentLogs));
            row.put("status", patientStatus(patient, meds, recentLogs));
            rows.add(row);
        }
        rows.sort(Comparator.comparing(row -> String.valueOf(row.get("name"))));
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<?> patient(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String patientId) {
        User doctor = currentDoctor(principal);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User patient = resolvePatient(patientId, doctor);
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }

        List<MedicationLog> recentLogs = recentLogs();
        List<Medication> meds = medicationRepo.findByPatientUsername(patient.getUsername());
        List<Map<String, Object>> medicationRows = new ArrayList<>();
        for (Medication med : meds) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", med.getId());
            row.put("name", med.getMedicationName());
            row.put("dosage", med.getDosage());
            row.put("time", formatTime(med.getScheduledTime()));
            row.put("status", medicationStatus(med, recentLogs, LocalDate.now()));
            medicationRows.add(row);
        }

        List<Map<String, Object>> activity = new ArrayList<>();
        for (MedicationLog log : logsFor(patient.getUsername(), recentLogs)) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", log.getId());
            row.put("time", formatTime(log.getEventTime()));
            row.put("medication", log.getMedicationName());
            row.put("description", describe(log));
            activity.add(row);
        }
        if (activity.isEmpty()) {
            for (Medication med : meds) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", med.getId());
                row.put("time", formatTime(med.getScheduledTime()));
                row.put("medication", med.getMedicationName());
                row.put("description", "Scheduled");
                activity.add(row);
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", patient.getId());
        body.put("name", patient.getUsername());
        body.put("patientUsername", patient.getUsername());
        body.put("username", patient.getUsername());
        body.put("doctorName", doctor.getUsername());
        body.put("status", patientStatus(patient, meds, recentLogs));
        body.put("adherence", adherence(meds, recentLogs));
        body.put("medications", medicationRows);
        body.put("activity", activity);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/patients/{patientId}/medications/{medicationId}")
    public ResponseEntity<?> medication(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String patientId,
            @PathVariable int medicationId) {
        User doctor = currentDoctor(principal);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User patient = resolvePatient(patientId, doctor);
        Medication med = medicationFor(patient, medicationId);
        if (med == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(medicationView(patient, med));
    }

    @PutMapping("/patients/{patientId}/medications/{medicationId}")
    public ResponseEntity<?> updateMedication(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String patientId,
            @PathVariable int medicationId,
            @RequestBody MedicationChange change) {

        User doctor = currentDoctor(principal);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User patient = resolvePatient(patientId, doctor);
        Medication med = medicationFor(patient, medicationId);
        if (med == null) {
            return ResponseEntity.notFound().build();
        }
        if (change.getName() != null) {
            med.setMedicationName(change.getName());
        }
        if (change.getDosage() != null) {
            med.setDosage(change.getDosage());
        }

        if (change.getTowerToDispense() != 0) {
            med.setTowerToDispense(change.getTowerToDispense());
        }

        if (change.getTime() != null && !change.getTime().isBlank()) {
            LocalTime parsed = parseTime(change.getTime());
            if (parsed == null) {
                return ResponseEntity.badRequest().body("Time must be HH:mm");
            }
            med.setScheduledTime(parsed);
        }
        medicationRepo.save(med);
        return ResponseEntity.ok(medicationView(patient, med));
    }

    @DeleteMapping("/patients/{patientId}/medications/{medicationId}")
    public ResponseEntity<?> deleteMedication(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String patientId,
            @PathVariable int medicationId) {
        User doctor = currentDoctor(principal);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User patient = resolvePatient(patientId, doctor);
        Medication med = medicationFor(patient, medicationId);
        if (med == null) {
            return ResponseEntity.notFound().build();
        }
        medicationRepo.delete(med);
        return ResponseEntity.ok("Medication removed successfully.");
    }

    @GetMapping("/medication-history")
    public ResponseEntity<?> history(@AuthenticationPrincipal UserDetails principal) {
        User doctor = currentDoctor(principal);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        java.util.Set<String> assignedUsernames = patients(doctor.getId()).stream()
                .map(User::getUsername)
                .collect(java.util.stream.Collectors.toSet());
        Map<Integer, Medication> medsById = new LinkedHashMap<>();
        for (Medication med : medicationRepo.findAll()) {
            medsById.put(med.getId(), med);
        }

        List<Map<String, Object>> events = new ArrayList<>();
        for (MedicationLog log : logRepo.findAllByOrderByEventTimeDesc()) {
            if (!assignedUsernames.contains(log.getPatientUsername())) {
                continue;
            }
            Medication med = medsById.get(log.getMedicationId());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", log.getId());
            row.put("patient", log.getPatientUsername());
            row.put("medication", log.getMedicationName());
            row.put("time", formatTime(log.getEventTime()));
            row.put("dose", med != null && med.getDosage() != null ? med.getDosage() : "");
            row.put("status", statusFromEvent(log.getEventType()));
            events.add(row);
        }
        return ResponseEntity.ok(events);
    }

    private Map<String, Object> medicationView(User patient, Medication med) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", med.getId());
        body.put("patientName", patient.getUsername());
        body.put("name", med.getMedicationName());
        body.put("dosage", med.getDosage());
        body.put("time", formatTime(med.getScheduledTime()));
        body.put("towerToDispense", med.getTowerToDispense());
        return body;
    }

    private User currentDoctor(UserDetails principal) {
        if (principal == null) {
            return null;
        }
        User doctor = userRepo.findByUsername(principal.getUsername());
        return doctor != null && doctor.isActive() && hasRole(doctor, "DOCTOR") ? doctor : null;
    }

    private List<User> patients(int doctorId) {
        List<User> patients = new ArrayList<>();
        for (DoctorPatientAssignment assignment : assignmentRepo.findByDoctor_IdOrderByPatient_UsernameAsc(doctorId)) {
            User patient = assignment.getPatient();
            if (patient.isActive() && hasRole(patient, "PATIENT")) {
                patients.add(patient);
            }
        }
        return patients;
    }

    private User resolvePatient(String patientId, User doctor) {
        if (patientId == null || patientId.isBlank()) {
            return null;
        }
        User patient = null;
        try {
            patient = userRepo.findById(Integer.parseInt(patientId)).orElse(null);
        } catch (NumberFormatException ignored) {
            patient = null;
        }
        if (patient == null) {
            patient = userRepo.findByUsername(patientId);
        }
        if (patient == null || !patient.isActive() || !hasRole(patient, "PATIENT")) {
            return null;
        }
        if (doctor == null || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), patient.getId())) {
            return null;
        }
        return patient;
    }

    private Medication medicationFor(User patient, int medicationId) {
        if (patient == null) {
            return null;
        }
        return medicationRepo.findById(medicationId)
                .filter(med -> patient.getUsername().equals(med.getPatientUsername()))
                .orElse(null);
    }

    private List<MedicationLog> recentLogs() {
        return logRepo.findByEventTimeAfterOrderByEventTimeDesc(
                LocalDate.now().minusDays(6).atStartOfDay()
        );
    }

    private List<MedicationLog> logsFor(String username, List<MedicationLog> logs) {
        List<MedicationLog> matches = new ArrayList<>();
        for (MedicationLog log : logs) {
            if (username.equals(log.getPatientUsername())) {
                matches.add(log);
            }
        }
        return matches;
    }

    private boolean hasRole(User user, String role) {
        if (user.getRole() == null) {
            return false;
        }
        String value = user.getRole().toUpperCase();
        if (value.startsWith("ROLE_")) {
            value = value.substring(5);
        }
        return value.equals(role);
    }

    private String medicationStatus(Medication med, List<MedicationLog> logs, LocalDate day) {
        for (MedicationLog log : logs) {
            if (log.getMedicationId() != med.getId() || log.getEventTime() == null) {
                continue;
            }
            if (!log.getEventTime().toLocalDate().equals(day)) {
                continue;
            }
            String status = statusFromEvent(log.getEventType());
            if ("Taken".equals(status) || "Missed".equals(status)) {
                return status;
            }
        }
        if (med.getScheduledTime() != null
                && day.equals(LocalDate.now())
                && LocalTime.now().isAfter(med.getScheduledTime())) {
            return "Missed";
        }
        return "Scheduled";
    }

    private String patientStatus(User patient, List<Medication> meds, List<MedicationLog> logs) {
        for (Medication med : meds) {
            if ("Missed".equals(medicationStatus(med, logs, LocalDate.now()))) {
                return "Attention";
            }
        }
        return "Active";
    }

    private int adherence(List<Medication> meds, List<MedicationLog> logs) {
        if (meds.isEmpty()) {
            return 100;
        }
        LocalDate today = LocalDate.now();
        int expected = 0;
        int taken = 0;
        for (int offset = 0; offset < 7; offset++) {
            LocalDate day = today.minusDays(offset);
            for (Medication med : meds) {
                boolean due = day.isBefore(today)
                        || (med.getScheduledTime() != null && !LocalTime.now().isBefore(med.getScheduledTime()));
                if (!due) {
                    continue;
                }
                expected++;
                for (MedicationLog log : logs) {
                    if (log.getMedicationId() == med.getId()
                            && log.getEventTime() != null
                            && log.getEventTime().toLocalDate().equals(day)
                            && "Taken".equals(statusFromEvent(log.getEventType()))) {
                        taken++;
                        break;
                    }
                }
            }
        }
        if (expected == 0) {
            return 100;
        }
        return (int) Math.round(taken * 100.0 / expected);
    }


    private String statusFromEvent(String eventType) {
        if (eventType == null) {
            return "Scheduled";
        }
        String value = eventType.toUpperCase();
        if (value.contains("DISPENS") || value.contains("TAKEN") || value.contains("COLLECT")) {
            return "Taken";
        }
        if (value.contains("MISS")) {
            return "Missed";
        }
        return "Scheduled";
    }

    private String describe(MedicationLog log) {
        if (log.getDetails() != null && !log.getDetails().isBlank()) {
            return log.getDetails();
        }
        return switch (statusFromEvent(log.getEventType())) {
            case "Taken" -> "Medication taken";
            case "Missed" -> "Scheduled medication dose has been missed";
            default -> "Scheduled";
        };
    }

    private String formatTime(LocalTime time) {
        return time == null ? "" : time.format(HM);
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? "" : time.toLocalTime().format(HM);
    }

    private LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    public static class MedicationChange {
        private String name;
        private String dosage;
        private String time;
        private int towerToDispense = 0;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getTowerToDispense() {
            return towerToDispense;
        }

        public void setTowerToDispense(int towerToDispense) {
            this.towerToDispense = towerToDispense;
        }
    }
}
