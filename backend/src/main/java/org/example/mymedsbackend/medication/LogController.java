package org.example.mymedsbackend.medication;

import org.example.mymedsbackend.user.User;
import org.example.mymedsbackend.user.DoctorPatientAssignment;
import org.example.mymedsbackend.user.DoctorPatientAssignmentRepo;
import org.example.mymedsbackend.user.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final MedicationLogRepo repo;
    private final UserRepo userRepo;
    private final DoctorPatientAssignmentRepo assignmentRepo;

    public LogController(MedicationLogRepo repo, UserRepo userRepo, DoctorPatientAssignmentRepo assignmentRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.assignmentRepo = assignmentRepo;
    }

    @GetMapping
    public ResponseEntity<List<MedicationLog>> getLogs(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                repo.findByPatientUsernameOrderByEventTimeDesc(user.getUsername())
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<MedicationLog>> getAllLogs(@AuthenticationPrincipal UserDetails user) {
        User account = userRepo.findByUsername(user.getUsername());
        if (account != null && "DOCTOR".equalsIgnoreCase(account.getRole().replace("ROLE_", ""))) {
            Set<String> assignedUsernames = assignmentRepo.findByDoctor_IdOrderByPatient_UsernameAsc(account.getId()).stream()
                    .map(DoctorPatientAssignment::getPatient)
                    .map(User::getUsername)
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(repo.findAllByOrderByEventTimeDesc().stream()
                    .filter(log -> assignedUsernames.contains(log.getPatientUsername()))
                    .toList());
        }
        return ResponseEntity.ok(repo.findAllByOrderByEventTimeDesc());
    }
}
