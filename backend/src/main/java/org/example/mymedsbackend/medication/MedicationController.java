package org.example.mymedsbackend.medication;

import org.example.mymedsbackend.user.DoctorPatientAssignment;
import org.example.mymedsbackend.user.DoctorPatientAssignmentRepo;
import org.example.mymedsbackend.user.User;
import org.example.mymedsbackend.user.UserRepo;
import org.example.mymedsbackend.util.MedicationUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/medications")
public class MedicationController {
    private final MedicationRepo medicationRepo;
    private final UserRepo userRepo;
    private final DoctorPatientAssignmentRepo assignmentRepo;


    public MedicationController(MedicationRepo medicationRepo, UserRepo drepo, DoctorPatientAssignmentRepo assignmentRepo) {
        this.medicationRepo = medicationRepo;
        this.userRepo = drepo;
        this.assignmentRepo = assignmentRepo;
    }

    @PostMapping
    public ResponseEntity<?> addMedication(@RequestBody Medication med, @AuthenticationPrincipal UserDetails principal) {
        User doctor = currentDoctor(principal);
        User patient = med.getPatient() == null ? null : userRepo.findById(med.getPatient().getId()).orElse(null);

        if (doctor == null || patient == null || !isPatient(patient) || !patient.isActive()
                || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), patient.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
        }
        med.setPatient(patient);
        medicationRepo.save(med);
        return ResponseEntity.ok("Successfully added " + med.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedication(@PathVariable int id, @AuthenticationPrincipal UserDetails principal) {
        Medication existing = medicationRepo.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(404).body("Medication not found");
        }
        User doctor = currentDoctor(principal);
        if (doctor == null || existing.getPatient() == null || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), existing.getPatient().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
        }
        medicationRepo.delete(existing);
        return ResponseEntity.ok("Medication removed successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedication(@PathVariable int id, @RequestBody Medication incoming, @AuthenticationPrincipal UserDetails principal) {
        Medication existing = medicationRepo.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(404).body("Medication not found");
        }
        User doctor = currentDoctor(principal);
        if (doctor == null || existing.getPatient() == null || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), existing.getPatient().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
        }
        if (incoming.getPatient() != null) {
            User newPatient = userRepo.findById(incoming.getPatient().getId()).orElse(null);
            if (newPatient == null || !isPatient(newPatient)
                    || !assignmentRepo.existsByDoctor_IdAndPatient_Id(doctor.getId(), newPatient.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient is not assigned to this doctor.");
            }
            incoming.setPatient(newPatient);
        }
        if (incoming.getName() != null) {
            existing.setName(incoming.getName());
        }
        if (incoming.getDosage() != null) {
            existing.setDosage(incoming.getDosage());
        }
        if (incoming.getScheduledTime() != null) {
            existing.setScheduledTime(incoming.getScheduledTime());
        }
        if (incoming.getTowerToDispense() != 0) {
            existing.setTowerToDispense(incoming.getTowerToDispense());
        }
        if (incoming.getPatient() != null) {
            existing.setPatient(incoming.getPatient());
        }
        return ResponseEntity.ok(medicationRepo.save(existing));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Medication>> getMyMedication(@AuthenticationPrincipal UserDetails user) {
        User patient = userRepo.findByUsername(user.getUsername());
        List<Medication> myMeds = patient == null
                ? List.of()
                : medicationRepo.findByPatient_Id(patient.getId());
        return ResponseEntity.ok(myMeds);
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientMedicationsResponse>> getAllPatientsWithMedication(@AuthenticationPrincipal UserDetails principal) {
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
            List<Medication> meds = medicationRepo.findByPatient_Id(patient.getId());
            responseList.add(new PatientMedicationsResponse(
                    patient.getUsername(),
                    patient.getEmail(),
                    meds
            ));
        }
        return ResponseEntity.ok(responseList);
    }


    @GetMapping("/next")
    public ResponseEntity<Medication> getNextMedicine(@AuthenticationPrincipal UserDetails user) {
        User patient = userRepo.findByUsername(user.getUsername());
        List<Medication> myMeds = patient == null
                ? List.of()
                : medicationRepo.findByPatient_Id(patient.getId());
        Medication nextMedication = MedicationUtil.nextDose(myMeds);

        if (nextMedication == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(nextMedication);
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
