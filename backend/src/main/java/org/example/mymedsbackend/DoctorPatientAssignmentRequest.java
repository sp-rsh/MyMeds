package org.example.mymedsbackend;

import java.util.List;

public class DoctorPatientAssignmentRequest {
    private List<Integer> patientIds;

    public List<Integer> getPatientIds() { return patientIds; }
    public void setPatientIds(List<Integer> patientIds) { this.patientIds = patientIds; }
}
