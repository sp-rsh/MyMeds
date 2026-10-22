package org.example.mymedsbackend.medication;

import java.util.List;

public record PatientMedicationsResponse(String patientUsername, String email, List<Medications> medications) {

}
