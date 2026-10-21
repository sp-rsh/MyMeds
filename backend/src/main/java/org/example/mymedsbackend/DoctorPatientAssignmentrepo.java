package org.example.mymedsbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DoctorPatientAssignmentrepo extends JpaRepository<DoctorPatientAssignment, Integer> {
    List<DoctorPatientAssignment> findByDoctor_IdOrderByPatient_UsernameAsc(int doctorId);
    List<DoctorPatientAssignment> findByPatient_Id(int patientId);
    Optional<DoctorPatientAssignment> findByDoctor_IdAndPatient_Id(int doctorId, int patientId);
    boolean existsByDoctor_IdAndPatient_Id(int doctorId, int patientId);
}
