package org.example.mymedsbackend.util;

import org.example.mymedsbackend.medication.Medication;

import java.time.LocalTime;
import java.util.List;

public class MedicationUtil {

    public static Medication nextDose(List<Medication> meds) {
        LocalTime now = LocalTime.now();
        Medication upcoming = null;
        Medication earliest = null;
        for (Medication med : meds) {
            if (med.getScheduledTime() == null) {
                continue;
            }
            if (earliest == null || med.getScheduledTime().isBefore(earliest.getScheduledTime())) {
                earliest = med;
            }
            if (!med.getScheduledTime().isBefore(now)
                    && (upcoming == null || med.getScheduledTime().isBefore(upcoming.getScheduledTime()))) {
                upcoming = med;
            }
        }
        return upcoming != null ? upcoming : earliest;
    }
}
