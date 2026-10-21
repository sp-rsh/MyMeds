package org.example.mymedsbackend;

import java.time.LocalDateTime;

public record UserAdminResponse(
        int id,
        String username,
        String name,
        String email,
        String role,
        boolean active,
        String status,
        int medications,
        int assignedPatients,
        LocalDateTime lastLoginAt,
        String specialization,
        String doctor
) {
    public static UserAdminResponse from(Credentials user, int medicationCount, int assignedPatientCount, String doctor) {
        return new UserAdminResponse(
                user.getId(),
                user.getUsername(),
                user.getUsername(),
                user.getEmail(),
                UserController.normaliseRole(user.getRole()),
                user.isActive(),
                user.isActive() ? "Active" : "Inactive",
                medicationCount,
                assignedPatientCount,
                user.getLastLoginAt(),
                user.getSpecialization(),
                doctor
        );
    }
}
