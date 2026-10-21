package org.example.mymedsbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface Medicationrepo extends JpaRepository<Medications, Integer> {
    List<Medications> findByPatientusername(String patientusername);
}