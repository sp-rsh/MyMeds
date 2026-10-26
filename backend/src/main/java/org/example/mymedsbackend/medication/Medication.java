package org.example.mymedsbackend.medication;

import jakarta.persistence.*;
import org.example.mymedsbackend.user.User;

import java.time.LocalTime;

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

    public int getId() {
        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String medicationName) {
        this.name = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public int getTowerToDispense() {
        return towerToDispense;
    }

    public void setTowerToDispense(int towerToDispense) {
        this.towerToDispense = towerToDispense;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User user) {
        this.patient = user;
    }
}
