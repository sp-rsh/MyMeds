package org.example.mymedsbackend.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.mymedsbackend.medication.Medication;
import org.example.mymedsbackend.medication.MedicationLog;
import org.example.mymedsbackend.medication.MedicationLogRepo;
import org.example.mymedsbackend.medication.MedicationRepo;
import org.example.mymedsbackend.security.SecurityLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Set<String> ROLES = Set.of("ADMIN", "DOCTOR", "PATIENT", "DEVICE");

    private final UserRepo repo;
    private final PasswordEncoder passwordEncoder;
    private final MedicationRepo medicationRepo;
    private final MedicationLogRepo logRepo;
    private final DoctorPatientAssignmentRepo assignmentRepo;

    private final SecurityLogService securityLog;

    public UserController(
            UserRepo repo,
            PasswordEncoder passwordEncoder,
            MedicationRepo medicationRepo,
            MedicationLogRepo logRepo,
            DoctorPatientAssignmentRepo assignmentRepo,
            SecurityLogService securityLog) {

        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.medicationRepo = medicationRepo;
        this.logRepo = logRepo;
        this.assignmentRepo = assignmentRepo;
        this.securityLog = securityLog;
    }

    static String normaliseRole(String role) {
        String normalised = role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
        if (normalised.startsWith("ROLE_")) normalised = normalised.substring(5);
        if (!ROLES.contains(normalised)) {
            throw new IllegalArgumentException("Role must be ADMIN, DOCTOR, or PATIENT.");
        }
        return normalised;
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim();
    }

    private static ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("message", message));
    }

    @GetMapping("/home")
    @Transactional
    public ResponseEntity<String> home(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = repo.findByUsername(userDetails.getUsername());
        if (user == null || !user.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        user.setLastLoginAt(LocalDateTime.now());
        repo.save(user);


        securityLog.record("Login", user.getUsername(), "Login", "Success", "Successful authentication");
        return ResponseEntity.ok("Login successful! Welcome to MyMeds API.");
    }



    @GetMapping("/users/me")
    public ResponseEntity<?> currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = repo.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", user.getId());
        body.put("username", user.getUsername());
        body.put("name", user.getUsername());
        body.put("email", user.getEmail());
        body.put("role", normaliseRole(user.getRole()));
        body.put("active", user.isActive());
        body.put("lastLoginAt", user.getLastLoginAt());

        if ("PATIENT".equals(normaliseRole(user.getRole()))) {
            body.put("deviceKey", user.getDeviceKey());

            body.put("doctor", assignmentRepo.findByPatient_Id(user.getId()).stream()
                    .map(a -> a.getDoctor().getUsername())
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(java.util.stream.Collectors.joining(", ")));
        }

        return ResponseEntity.ok(body);
    }

    @GetMapping("/users")
    public ResponseEntity<?> listUsers(@RequestParam(required = false) String role) {
        String parsedRole = null;
        if (role != null && !role.isBlank() && !"ALL".equalsIgnoreCase(role)) {
            try {
                parsedRole = normaliseRole(role);
            } catch (IllegalArgumentException ex) {
                return error(HttpStatus.BAD_REQUEST, ex.getMessage());
            }
        }
        final String roleFilter = parsedRole;

        List<UserAdminResponse> users = repo.findAllByOrderByUsernameAsc().stream()
                .filter(user -> roleFilter == null || roleFilter.equals(normaliseRole(user.getRole())))
                .map(this::userResponse)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{doctorId}/patients")
    public ResponseEntity<?> getDoctorPatients(@PathVariable int doctorId) {
        User doctor = repo.findById(doctorId).orElse(null);
        if (doctor == null || !"DOCTOR".equals(normaliseRole(doctor.getRole()))) {
            return error(HttpStatus.NOT_FOUND, "Doctor account not found.");
        }
        List<Map<String, Object>> patients = assignmentRepo.findByDoctor_IdOrderByPatient_UsernameAsc(doctorId).stream()
                .map(assignment -> {
                    User patient = assignment.getPatient();
                    Map<String, Object> body = new LinkedHashMap<>();
                    body.put("id", patient.getId());
                    body.put("username", patient.getUsername());
                    body.put("email", patient.getEmail());
                    body.put("active", patient.isActive());
                    body.put("assignedAt", assignment.getAssignedAt());
                    return body;
                })
                .toList();
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/users/{doctorId}/patients")
    @Transactional
    public ResponseEntity<?> replaceDoctorPatients(
            @PathVariable int doctorId,
            @RequestBody DoctorPatientAssignmentRequest request) {
        User doctor = repo.findById(doctorId).orElse(null);
        if (doctor == null || !"DOCTOR".equals(normaliseRole(doctor.getRole()))) {
            return error(HttpStatus.NOT_FOUND, "Doctor account not found.");
        }
        if (request == null || request.getPatientIds() == null) {
            return error(HttpStatus.BAD_REQUEST, "patientIds is required.");
        }

        Set<Integer> patientIds = new LinkedHashSet<>(request.getPatientIds());
        List<User> patients = new java.util.ArrayList<>();
        for (Integer patientId : patientIds) {
            if (patientId == null) {
                return error(HttpStatus.BAD_REQUEST, "Patient IDs must be valid account IDs.");
            }
            User patient = repo.findById(patientId).orElse(null);
            if (patient == null || !"PATIENT".equals(normaliseRole(patient.getRole()))) {
                return error(HttpStatus.BAD_REQUEST, "Every assigned account must be an existing patient.");
            }
            patients.add(patient);
        }

        List<DoctorPatientAssignment> existingAssignments = assignmentRepo.findByDoctor_IdOrderByPatient_UsernameAsc(doctorId);
        Set<Integer> existingPatientIds = existingAssignments.stream()
                .map(assignment -> assignment.getPatient().getId())
                .collect(java.util.stream.Collectors.toSet());
        for (DoctorPatientAssignment assignment : existingAssignments) {
            if (!patientIds.contains(assignment.getPatient().getId())) {
                assignmentRepo.delete(assignment);
            }
        }
        List<DoctorPatientAssignment> assignmentsToAdd = patients.stream()
                .filter(patient -> !existingPatientIds.contains(patient.getId()))
                .map(patient -> {
                    DoctorPatientAssignment assignment = new DoctorPatientAssignment();
                    assignment.setDoctor(doctor);
                    assignment.setPatient(patient);
                    return assignment;
                })
                .toList();
        assignmentRepo.saveAll(assignmentsToAdd);
        return getDoctorPatients(doctorId);
    }

    @PostMapping("/users/register")
    @Transactional
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        String username = trimToNull(user.getUsername());
        String email = trimToNull(user.getEmail());
        String password = user.getPassword();
        String role;
        try {
            role = normaliseRole(user.getRole());
        } catch (IllegalArgumentException ex) {
            return error(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        if (username == null || email == null || password == null || password.length() < 8) {
            return error(HttpStatus.BAD_REQUEST, "Username, email, and a password of at least 8 characters are required.");
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return error(HttpStatus.BAD_REQUEST, "Enter a valid email address.");
        }
        if (repo.existsByUsernameIgnoreCase(username)) {
            return error(HttpStatus.CONFLICT, "That username is already in use.");
        }
        if (repo.existsByEmailIgnoreCase(email)) {
            return error(HttpStatus.CONFLICT, "That email address is already in use.");
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(password));
        user.setDeviceKey(username + "_" + UUID.randomUUID().toString().replace("-", ""));
        user.setActive(true);
        User saved = repo.save(user);
        securityLog.recordAsCurrentUser("Account created", "Account", "Success", role + " account " + saved.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse(saved));
    }

    @PutMapping("/users/{id}")
    @Transactional
    public ResponseEntity<?> updateUser(
            @PathVariable int id,
            @RequestBody UserAdminUpdateRequest request,
            @AuthenticationPrincipal UserDetails actor) {
        User user = repo.findById(id).orElse(null);
        if (user == null) return error(HttpStatus.NOT_FOUND, "User not found.");

        String username = trimToNull(request.getUsername());
        String email = trimToNull(request.getEmail());
        String password = request.getPassword();
        String role;
        try {
            role = normaliseRole(request.getRole());
        } catch (IllegalArgumentException ex) {
            return error(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        if (username == null || email == null) {
            return error(HttpStatus.BAD_REQUEST, "Username and email are required.");
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return error(HttpStatus.BAD_REQUEST, "Enter a valid email address.");
        }
        if (password != null && !password.isBlank() && password.length() < 8) {
            return error(HttpStatus.BAD_REQUEST, "A new password must contain at least 8 characters.");
        }
        boolean usernameUsedByAnotherAccount = repo.findAll().stream()
                .anyMatch(other -> other.getId() != id && other.getUsername() != null
                        && other.getUsername().equalsIgnoreCase(username));
        if (usernameUsedByAnotherAccount) {
            return error(HttpStatus.CONFLICT, "That username is already in use.");
        }
        if (repo.existsByEmailIgnoreCase(email)
                && (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(email))) {
            return error(HttpStatus.CONFLICT, "That email address is already in use.");
        }

        boolean isActor = actor != null && actor.getUsername().equals(user.getUsername());
        if (isActor && !user.getUsername().equals(username)) {
            return error(HttpStatus.CONFLICT, "You cannot rename your own account during the current session.");
        }
        if (isActor && !"ADMIN".equals(role)) {
            return error(HttpStatus.CONFLICT, "You cannot remove your own administrator role.");
        }
        if (user.isActive() && "ADMIN".equals(normaliseRole(user.getRole())) && !"ADMIN".equals(role) && countActiveAdmins() <= 1) {
            return error(HttpStatus.CONFLICT, "The last administrator cannot be demoted.");
        }

        boolean hasPatientData = !medicationRepo.findByPatient_Id(user.getId()).isEmpty()
                || !logRepo.findByPatient_IdOrderByEventTimeDesc(user.getId()).isEmpty();
        if ("PATIENT".equals(normaliseRole(user.getRole())) && hasPatientData && !"PATIENT".equals(role)) {
            return error(HttpStatus.CONFLICT, "This account has medication history. Keep its role as PATIENT to preserve access to those records.");
        }

        String previousUsername = user.getUsername();
        if (!normaliseRole(user.getRole()).equals(role)) {
            clearAssignments(user);
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        if (password != null && !password.isBlank()) user.setPassword(passwordEncoder.encode(password));
        User saved = repo.save(user);
        return ResponseEntity.ok(userResponse(saved));
    }

    @PatchMapping("/users/{id}/role")
    @Transactional
    public ResponseEntity<?> updateRole(
            @PathVariable int id,
            @RequestBody UserRoleRequest request,
            @AuthenticationPrincipal UserDetails actor) {
        User user = repo.findById(id).orElse(null);
        if (user == null) return error(HttpStatus.NOT_FOUND, "User not found.");
        String role;
        try {
            role = normaliseRole(request.getRole());
        } catch (IllegalArgumentException ex) {
            return error(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        if (actor != null && actor.getUsername().equals(user.getUsername()) && !"ADMIN".equals(role)) {
            return error(HttpStatus.CONFLICT, "You cannot remove your own administrator role.");
        }
        if (user.isActive() && "ADMIN".equals(normaliseRole(user.getRole())) && !"ADMIN".equals(role) && countActiveAdmins() <= 1) {
            return error(HttpStatus.CONFLICT, "The last administrator cannot be demoted.");
        }
        boolean hasPatientData = !medicationRepo.findByPatient_Id(user.getId()).isEmpty()
                || !logRepo.findByPatient_IdOrderByEventTimeDesc(user.getId()).isEmpty();
        if ("PATIENT".equals(normaliseRole(user.getRole())) && hasPatientData && !"PATIENT".equals(role)) {
            return error(HttpStatus.CONFLICT, "This account has medication history. Keep its role as PATIENT to preserve access to those records.");
        }
        if (!normaliseRole(user.getRole()).equals(role)) {
            clearAssignments(user);
        }
        user.setRole(role);
        User saved = repo.save(user);
        securityLog.recordAsCurrentUser("Role updated", "Role Change", "Success", saved.getUsername() + " is now " + role);
        return ResponseEntity.ok(userResponse(saved));
    }

    @PatchMapping("/users/{id}/status")
    @Transactional
    public ResponseEntity<?> updateStatus(
            @PathVariable int id,
            @RequestBody UserStatusRequest request,
            @AuthenticationPrincipal UserDetails actor) {
        if (request.getActive() == null) {
            return error(HttpStatus.BAD_REQUEST, "The active field is required.");
        }
        User user = repo.findById(id).orElse(null);
        if (user == null) return error(HttpStatus.NOT_FOUND, "User not found.");
        if (actor != null && actor.getUsername().equals(user.getUsername()) && !request.getActive()) {
            return error(HttpStatus.CONFLICT, "You cannot deactivate your own account.");
        }
        if (!request.getActive() && user.isActive() && "ADMIN".equals(normaliseRole(user.getRole())) && countActiveAdmins() <= 1) {
            return error(HttpStatus.CONFLICT, "The last administrator cannot be deactivated.");
        }
        user.setActive(request.getActive());
        User saved = repo.save(user);
        securityLog.recordAsCurrentUser(saved.isActive() ? "Account activated" : "Account deactivated", "Account", "Success", saved.getUsername());
        return ResponseEntity.ok(userResponse(saved));
    }

    record ResetPassword(String newPassword, String oldPassword) {
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ResetPassword request) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = repo.findByUsername(userDetails.getUsername());
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect current password!");
        }
        if (request.newPassword() == null || request.newPassword().length() < 8) {
            return ResponseEntity.badRequest().body("A new password must contain at least 8 characters.");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        repo.save(user);
        return ResponseEntity.ok("Password updated successfully!");
    }

    private UserAdminResponse userResponse(User user) {
        String role = normaliseRole(user.getRole());
        int medicationCount = "PATIENT".equals(role)
                ? medicationRepo.findByPatient_Id(user.getId()).size() : 0;
        int assignedPatientCount = "DOCTOR".equals(role)
                ? assignmentRepo.findByDoctor_IdOrderByPatient_UsernameAsc(user.getId()).size() : 0;
        String doctor = "PATIENT".equals(role)
                ? assignmentRepo.findByPatient_Id(user.getId()).stream()
                .map(assignment -> assignment.getDoctor().getUsername())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(java.util.stream.Collectors.joining(", "))
                : "";
        return UserAdminResponse.from(user, medicationCount, assignedPatientCount, doctor);
    }

    private void clearAssignments(User user) {
        assignmentRepo.deleteAll(assignmentRepo.findByDoctor_IdOrderByPatient_UsernameAsc(user.getId()));
        assignmentRepo.deleteAll(assignmentRepo.findByPatient_Id(user.getId()));
    }

    private long countActiveAdmins() {
        return repo.findAll().stream()
                .filter(User::isActive)
                .filter(user -> "ADMIN".equals(normaliseRole(user.getRole())))
                .count();
    }
}