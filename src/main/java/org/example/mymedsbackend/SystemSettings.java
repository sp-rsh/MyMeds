package org.example.mymedsbackend;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SystemSettings {

    @Id
    private int id = 1;

    private String systemName = "MyMeds";
    private String timezone = "Europe/Amsterdam";
    private boolean maintenance = false;
    private boolean rateLimit = true;
    private boolean auditLogs = true;
    private boolean autoDispense = true;
    private boolean intrusion = true;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public boolean isMaintenance() { return maintenance; }
    public void setMaintenance(boolean maintenance) { this.maintenance = maintenance; }
    public boolean isRateLimit() { return rateLimit; }
    public void setRateLimit(boolean rateLimit) { this.rateLimit = rateLimit; }
    public boolean isAuditLogs() { return auditLogs; }
    public void setAuditLogs(boolean auditLogs) { this.auditLogs = auditLogs; }
    public boolean isAutoDispense() { return autoDispense; }
    public void setAutoDispense(boolean autoDispense) { this.autoDispense = autoDispense; }
    public boolean isIntrusion() { return intrusion; }
    public void setIntrusion(boolean intrusion) { this.intrusion = intrusion; }
}