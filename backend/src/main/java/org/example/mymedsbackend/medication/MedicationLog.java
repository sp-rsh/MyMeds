package org.example.mymedsbackend.medication;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.mymedsbackend.user.User;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class MedicationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    private LocalDateTime eventTime;
}
