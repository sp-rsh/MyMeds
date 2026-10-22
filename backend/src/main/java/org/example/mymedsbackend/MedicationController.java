package org.example.mymedsbackend;

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
    private final Medicationrepo repo;
    private final Userrepo drepo;
    private final DoctorPatientAssignmentrepo assignmentRepo;
    public MedicationController(Medicationrepo repo, Userrepo drepo, DoctorPatientAssignmentrepo assignmentRepo) {
        this.repo = repo;
        this.drepo = drepo;
        this.assignmentRepo = assignmentRepo;
    }
    @PostMapping
    public ResponseEntity<?> addMedications(@RequestBody Medications medications, @AuthenticationPrincipal UserDetails principal){
        Credentials doctor = currentDoctor(principal);
        Credentials patient = medications.getPatientusername() == null ? null : drepo.findByUsername(medications.getPatientusername());
        if (doctor == null || patient == null || !isPatient(patient) || !patient.isActive()
                || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), patient.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
        }
        if (medications.getFrequency() == null || medications.getFrequency().isBlank()) {
            medications.setFrequency("Once daily");
        }
        repo.save(medications);
        return ResponseEntity.ok("Successfully added " + medications.getMedicationName());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedication(@PathVariable int id, @AuthenticationPrincipal UserDetails principal) {
        Medications existing = repo.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(404).body("Medication not found");
        }
        Credentials doctor = currentDoctor(principal);
        Credentials patient = drepo.findByUsername(existing.getPatientusername());
        if (doctor == null || patient == null || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), patient.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
        }
        repo.delete(existing);
        return ResponseEntity.ok("Medication removed successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedication(@PathVariable int id, @RequestBody Medications incoming, @AuthenticationPrincipal UserDetails principal) {
        Medications existing = repo.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(404).body("Medication not found");
        }
        Credentials doctor = currentDoctor(principal);
        Credentials existingPatient = drepo.findByUsername(existing.getPatientusername());
        if (doctor == null || existingPatient == null || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), existingPatient.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
        }
        if (incoming.getPatientusername() != null) {
            Credentials newPatient = drepo.findByUsername(incoming.getPatientusername());
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
        if (incoming.getFrequency() != null) {
            existing.setFrequency(incoming.getFrequency());
        }
        if (incoming.getPatientusername() != null) {
            existing.setPatientusername(incoming.getPatientusername());
        }
        return ResponseEntity.ok(repo.save(existing));
    }
    @GetMapping("/my")
    public ResponseEntity<List<Medications>> getMyMedications(@AuthenticationPrincipal UserDetails user) {
        List<Medications> myMeds = repo.findByPatientusername(user.getUsername());
        return ResponseEntity.ok(myMeds);
    }
    @GetMapping("/patients")
    public ResponseEntity<List<PatientMedicationsResponse>> getAllPatientsWithMedications(@AuthenticationPrincipal UserDetails principal) {
        Credentials doctor = currentDoctor(principal);
        List<Credentials> patients = new ArrayList<>();
        if (doctor != null) {
            for (DoctorPatientAssignment assignment : assignmentRepo.findByDoctor_IdOrderByPatient_UsernameAsc(doctor.getId())) {
                Credentials patient = assignment.getPatient();
                if (patient.isActive() && isPatient(patient)) patients.add(patient);
            }
        }
        List<PatientMedicationsResponse> responseList = new ArrayList<>();
        for (Credentials patient : patients) {
            List<Medications> meds = repo.findByPatientusername(patient.getUsername());
            responseList.add(new PatientMedicationsResponse(
                    patient.getUsername(),
                    patient.getEmail(),
                    meds
            ));
        }
        return ResponseEntity.ok(responseList);
    }

    private Credentials currentDoctor(UserDetails principal) {
        if (principal == null) return null;
        Credentials doctor = drepo.findByUsername(principal.getUsername());
        if (doctor == null || !doctor.isActive() || doctor.getRole() == null) return null;
        String role = doctor.getRole().toUpperCase();
        return "DOCTOR".equals(role) ? doctor : null;
    }

    private boolean isPatient(Credentials user) {
        if (user.getRole() == null) {
            return false;
        }
        String role = user.getRole().toUpperCase();
        return "PATIENT".equals(role);
    }
}

