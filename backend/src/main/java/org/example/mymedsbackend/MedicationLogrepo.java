package org.example.mymedsbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MedicationLogrepo extends JpaRepository<MedicationLog, Integer> {
    List<MedicationLog> findByPatientusernameOrderByEventTimeDesc(String patientusername);

    List<MedicationLog> findByPatientusernameAndEventTypeAndEventTimeBetween(
            String patientusername,
            String eventType,
            LocalDateTime start,
            LocalDateTime end
    );
    List<MedicationLog> findAllByOrderByEventTimeDesc();

    List<MedicationLog> findByEventTimeAfterOrderByEventTimeDesc(LocalDateTime localDateTime);

}