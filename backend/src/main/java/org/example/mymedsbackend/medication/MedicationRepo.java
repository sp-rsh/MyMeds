package org.example.mymedsbackend.medication;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicationRepo extends JpaRepository<Medications, Integer> {
    List<Medications> findByPatientUsername(String patientUsername);
}