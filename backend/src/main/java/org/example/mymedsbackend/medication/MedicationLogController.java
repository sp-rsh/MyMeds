package org.example.mymedsbackend.medication;

import org.example.mymedsbackend.user.DoctorPatientAssignment;
import org.example.mymedsbackend.user.DoctorPatientAssignmentRepo;
import org.example.mymedsbackend.user.User;
import org.example.mymedsbackend.user.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class MedicationLogController {

    private final MedicationLogRepo repo;
    private final UserRepo userRepo;
    private final DoctorPatientAssignmentRepo assignmentRepo;
    private final MedicationRepo medicationRepo;

    public MedicationLogController(MedicationLogRepo repo, UserRepo userRepo, DoctorPatientAssignmentRepo assignmentRepo, MedicationRepo medicationRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.assignmentRepo = assignmentRepo;
        this.medicationRepo = medicationRepo;
    }

    @GetMapping
    public ResponseEntity<List<MedicationLog>> getLogs(
            @AuthenticationPrincipal UserDetails user) {
        User patient = userRepo.findByUsername(user.getUsername());
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                repo.findByPatient_IdOrderByEventTimeDesc(patient.getId())
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<MedicationLog>> getAllLogs(@AuthenticationPrincipal UserDetails user) {
        User account = userRepo.findByUsername(user.getUsername());
        if (account != null && "DOCTOR".equalsIgnoreCase(account.getRole().replace("ROLE_", ""))) {
            Set<Integer> assignedPatientIds = assignmentRepo.findByDoctor_IdOrderByPatient_UsernameAsc(account.getId()).stream()
                    .map(DoctorPatientAssignment::getPatient)
                    .map(User::getId)
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(repo.findAllByOrderByEventTimeDesc().stream()
                    .filter(log -> log.getPatient() != null && assignedPatientIds.contains(log.getPatient().getId()))
                    .toList());
        }
        return ResponseEntity.ok(repo.findAllByOrderByEventTimeDesc());
    }


    @PostMapping("/{id}")
    public ResponseEntity<?> postMedicineLog(@PathVariable int id, @AuthenticationPrincipal UserDetails details) {
        User account = userRepo.findByUsername(details.getUsername());
        Medication medication = medicationRepo.findByIdAndPatient_Id(id, account.getId());

        if (medication == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine was not found");
        }
        MedicationLog log = new MedicationLog();

        log.setMedication(medication);
        log.setPatient(medication.getPatient());
        log.setEventTime(LocalDateTime.now());

        repo.save(log);

        return ResponseEntity.status(HttpStatus.CREATED).body("Logged Medication");
    }
}
