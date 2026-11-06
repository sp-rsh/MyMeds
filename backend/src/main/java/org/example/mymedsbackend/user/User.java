package org.example.mymedsbackend.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String role;
    private LocalDateTime lastLoginAt;
    private boolean active = true;
    private boolean totpActive = false;
    private String totpSecret;

    private String deviceKey; // this is what our hardware uses to link to the user, UUID is secure
    private String discordID;
}
