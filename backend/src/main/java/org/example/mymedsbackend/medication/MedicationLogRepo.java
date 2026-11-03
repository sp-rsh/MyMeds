package org.example.mymedsbackend.medication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationLogRepo extends JpaRepository<MedicationLog, Integer> {
    List<MedicationLog> findByPatient_IdOrderByEventTimeDesc(int patientId);

    List<MedicationLog> findByPatient_IdAndEventTimeBetweenOrderByEventTimeDesc(
            int patientId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<MedicationLog> findAllByOrderByEventTimeDesc();

    List<MedicationLog> findByEventTimeAfterOrderByEventTimeDesc(LocalDateTime localDateTime);


    void deleteMedicationLogByMedication(Medication medication);

}