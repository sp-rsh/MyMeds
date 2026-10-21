package org.example.mymedsbackend;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class SecurityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime eventTime;
    private String eventName;
    private String username;
    private String eventType;
    private String result;
    private String details;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}