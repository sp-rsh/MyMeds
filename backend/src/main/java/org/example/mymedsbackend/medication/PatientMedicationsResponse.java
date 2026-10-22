package org.example.mymedsbackend.medication;

import java.util.List;

public class PatientMedicationsResponse {
    private String patientUsername;
    public String getPatientUsername() {
        return patientUsername;
    }
    public String getEmail() {
        return email;
    }
    private String email;

    public List<Medications> getMedications() {
        return medications;
    }
    private List<Medications> medications;
    public PatientMedicationsResponse(String patientUsername, String email, List<Medications> medications) {
        this.patientUsername = patientUsername;
        this.email = email;
        this.medications = medications;
    }

}
