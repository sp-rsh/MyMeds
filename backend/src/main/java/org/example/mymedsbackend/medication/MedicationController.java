package org.example.mymedsbackend.medication;

import org.example.mymedsbackend.user.User;
import org.example.mymedsbackend.user.DoctorPatientAssignment;
import org.example.mymedsbackend.user.DoctorPatientAssignmentrepo;
import org.example.mymedsbackend.user.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/medications")
public class MedicationController {
    private final MedicationRepo medicationRepo;
    private final UserRepo userRepo;
    private final DoctorPatientAssignmentrepo assignmentRepo;


    public MedicationController(MedicationRepo medicationRepo, UserRepo drepo, DoctorPatientAssignmentrepo assignmentRepo) {
        this.medicationRepo = medicationRepo;
        this.userRepo = drepo;
        this.assignmentRepo = assignmentRepo;
    }
    @PostMapping
    public ResponseEntity<?> addMedications(@RequestBody Medications medications, @AuthenticationPrincipal UserDetails principal){
        User doctor = currentDoctor(principal);
        User patient = medications.getPatientUsername() == null ? null : userRepo.findByUsername(medications.getPatientUsername());
        if (doctor == null || patient == null || !isPatient(patient) || !patient.isActive()
                || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), patient.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
        }
        medicationRepo.save(medications);
        return ResponseEntity.ok("Successfully added " + medications.getMedicationName());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedication(@PathVariable int id, @AuthenticationPrincipal UserDetails principal) {
        Medications existing = medicationRepo.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(404).body("Medication not found");
        }
        User doctor = currentDoctor(principal);
        User patient = userRepo.findByUsername(existing.getPatientUsername());
        if (doctor == null || patient == null || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), patient.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
        }
        medicationRepo.delete(existing);
        return ResponseEntity.ok("Medication removed successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedication(@PathVariable int id, @RequestBody Medications incoming, @AuthenticationPrincipal UserDetails principal) {
        Medications existing = medicationRepo.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(404).body("Medication not found");
        }
        User doctor = currentDoctor(principal);
        User existingPatient = userRepo.findByUsername(existing.getPatientUsername());
        if (doctor == null || existingPatient == null || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), existingPatient.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
        }
        if (incoming.getPatientUsername() != null) {
            User newPatient = userRepo.findByUsername(incoming.getPatientUsername());
            if (newPatient == null || !isPatient(newPatient) || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), newPatient.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
            }
        }
        if (incoming.getMedicationName() != null) {
            existing.setMedicationName(incoming.getMedicationName());
        }
        if (incoming.getDosage() != null) {
            existing.setDosage(incoming.getDosage());
        }
        if (incoming.getScheduledTime() != null) {
            existing.setScheduledTime(incoming.getScheduledTime());
        }

        if (incoming.getPatientUsername() != null) {
            existing.setPatientUsername(incoming.getPatientUsername());
        }
        return ResponseEntity.ok(medicationRepo.save(existing));
    }
    @GetMapping("/my")
    public ResponseEntity<List<Medications>> getMyMedications(@AuthenticationPrincipal UserDetails user) {
        List<Medications> myMeds = medicationRepo.findByPatientUsername(user.getUsername());
        return ResponseEntity.ok(myMeds);
    }
    @GetMapping("/patients")
    public ResponseEntity<List<PatientMedicationsResponse>> getAllPatientsWithMedications(@AuthenticationPrincipal UserDetails principal) {
        User doctor = currentDoctor(principal);
        List<User> patients = new ArrayList<>();
        if (doctor != null) {
            for (DoctorPatientAssignment assignment : assignmentRepo.findByDoctor_IdOrderByPatient_UsernameAsc(doctor.getId())) {
                User patient = assignment.getPatient();
                if (patient.isActive() && isPatient(patient)) patients.add(patient);
            }
        }
        List<PatientMedicationsResponse> responseList = new ArrayList<>();
        for (User patient : patients) {
            List<Medications> meds = medicationRepo.findByPatientUsername(patient.getUsername());
            responseList.add(new PatientMedicationsResponse(
                    patient.getUsername(),
                    patient.getEmail(),
                    meds
            ));
        }
        return ResponseEntity.ok(responseList);
    }

    private User currentDoctor(UserDetails principal) {
        if (principal == null) return null;
        User doctor = userRepo.findByUsername(principal.getUsername());
        if (doctor == null || !doctor.isActive() || doctor.getRole() == null) return null;
        String role = doctor.getRole().toUpperCase();
        return "DOCTOR".equals(role) ? doctor : null;
    }

    private boolean isPatient(User user) {
        if (user.getRole() == null) {
            return false;
        }
        String role = user.getRole().toUpperCase();
        return "PATIENT".equals(role);
    }
}

