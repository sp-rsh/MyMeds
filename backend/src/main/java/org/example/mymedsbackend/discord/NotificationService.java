package org.example.mymedsbackend.discord;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import org.example.mymedsbackend.medication.Medication;
import org.example.mymedsbackend.medication.MedicationRepo;
import org.example.mymedsbackend.user.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;

@AllArgsConstructor
@Service
public class NotificationService {

    private final MedicationRepo medicationRepo;
    private final JDA jda;

    @Scheduled(fixedDelay = 60_000) // every 60 seconds
    public void notifyUpcomingMedications() {
        if (jda == null) return;

        LocalTime now = LocalTime.now();

        for (Medication medication : medicationRepo.findAll()) {
            if (medication.getScheduledTime() == null) {
                continue;
            }

            long difference = Math.abs(Duration.between(medication.getScheduledTime(), now).getSeconds());

            if (difference > 30) {
                continue;
            }

            User patient = medication.getPatient();
            if (patient == null || !patient.isActive() || patient.getDiscordID() == null || patient.getDiscordID().isBlank()) {
                continue;
            }

            jda.retrieveUserById(patient.getDiscordID()).submit().thenCompose(user -> user.openPrivateChannel().submit())
                    .thenCompose(channel -> channel
                            .sendMessage("💊 It is time to take " + medication.getName())
                            .submit())

                    .exceptionally(error -> {
                        System.err.println("Could not message " + patient.getUsername() + " on discord!");
                        error.printStackTrace();
                        return null;
                    });
        }
    }


}
