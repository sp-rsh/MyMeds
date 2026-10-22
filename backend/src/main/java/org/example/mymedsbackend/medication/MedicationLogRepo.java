package org.example.mymedsbackend.medication;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MedicationLogRepo extends JpaRepository<MedicationLog, Integer> {
    List<MedicationLog> findByPatientUsernameOrderByEventTimeDesc(String patientUsername);

    List<MedicationLog> findByPatientUsernameAndEventTypeAndEventTimeBetween(
            String patientUsername,
            String eventType,
            LocalDateTime start,
            LocalDateTime end
    );
    List<MedicationLog> findAllByOrderByEventTimeDesc();

    List<MedicationLog> findByEventTimeAfterOrderByEventTimeDesc(LocalDateTime localDateTime);

}