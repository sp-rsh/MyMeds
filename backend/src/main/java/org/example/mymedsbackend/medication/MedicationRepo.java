package org.example.mymedsbackend.medication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepo extends JpaRepository<Medication, Integer> {
    List<Medication> findByPatient_Username(String username);

    List<Medication> findByPatient_Id(int patientId);
}