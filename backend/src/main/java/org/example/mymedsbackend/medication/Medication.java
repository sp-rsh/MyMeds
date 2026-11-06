package org.example.mymedsbackend.medication;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.mymedsbackend.user.User;

import java.time.LocalTime;

@Setter
@Getter
@Entity
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User patient;

    private String name;
    private String dosage;

    private LocalTime scheduledTime;
    private int towerToDispense = 0;
}
