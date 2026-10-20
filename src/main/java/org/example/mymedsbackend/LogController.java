package org.example.mymedsbackend;

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

    private final MedicationLogrepo repo;
    private final Databaserepo userRepo;
    private final DoctorPatientAssignmentrepo assignmentRepo;

    public LogController(MedicationLogrepo repo, Databaserepo userRepo, DoctorPatientAssignmentrepo assignmentRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.assignmentRepo = assignmentRepo;
    }

    @GetMapping
    public ResponseEntity<List<MedicationLog>> getLogs(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                repo.findByPatientusernameOrderByEventTimeDesc(user.getUsername())
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<MedicationLog>> getAllLogs(@AuthenticationPrincipal UserDetails user) {
        Credentials account = userRepo.findByUsername(user.getUsername());
        if (account != null && "DOCTOR".equalsIgnoreCase(account.getRole().replace("ROLE_", ""))) {
            Set<String> assignedUsernames = assignmentRepo.findByDoctor_IdOrderByPatient_UsernameAsc(account.getId()).stream()
                    .map(DoctorPatientAssignment::getPatient)
                    .map(Credentials::getUsername)
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(repo.findAllByOrderByEventTimeDesc().stream()
                    .filter(log -> assignedUsernames.contains(log.getPatientusername()))
                    .toList());
        }
        return ResponseEntity.ok(repo.findAllByOrderByEventTimeDesc());
    }
}
