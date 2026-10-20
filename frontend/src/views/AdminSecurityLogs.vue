<template>
  <div class="admin-page">
    <header class="page-header">
      <p class="eyebrow">
        ADMINISTRATION
      </p>

      <h1 class="page-title">
        Security Logs
      </h1>

      <p class="page-subtitle">
        Review security and administrative activity recorded by MyMeds.
      </p>
    </header>

    <section class="status-card">
      <div class="status-icon">
        <ShieldCheck :size="26" />
      </div>

      <div>
        <h2>
          Security logging
        </h2>

        <p>
          This page is ready to display audit events once security logging is
          connected to the backend.
        </p>
      </div>
    </section>

    <section class="panel">
      <div class="panel-header">
        <div>
          <h2>
            Recorded Events
          </h2>

          <p>
            Authentication, account, role, and access-control events will appear here.
          </p>
        </div>

        <Activity :size="22" />
      </div>

      <div class="empty-state">
        <div class="empty-icon">
          <FileClock :size="30" />
        </div>

        <h3>
          No security events available
        </h3>

        <p>
          Security events will be shown here once audit logging is connected
          to the MyMeds backend.
        </p>
      </div>
    </section>

    <section class="event-types">
      <h2>
        Events that can be recorded
      </h2>

      <div class="event-grid">
        <div class="event-card">
          <LogIn :size="21" />

          <div>
            <strong>
              Authentication
            </strong>

            <p>
              Successful and unsuccessful login attempts.
            </p>
          </div>
        </div>

        <div class="event-card">
          <Users :size="21" />

          <div>
            <strong>
              Account administration
            </strong>

            <p>
              Creation and modification of user accounts.
            </p>
          </div>
        </div>

        <div class="event-card">
          <ShieldCheck :size="21" />

          <div>
            <strong>
              Role changes
            </strong>

            <p>
              Changes to user roles and permissions.
            </p>
          </div>
        </div>

        <div class="event-card">
          <UserCheck :size="21" />

          <div>
            <strong>
              Patient assignments
            </strong>

            <p>
              Changes to doctor-patient assignments.
            </p>
          </div>
        </div>
      </div>
    </section>

    <section class="info-box">
      <div class="info-icon">
        <ShieldCheck :size="21" />
      </div>

      <div>
        <h3>
          Audit trail
        </h3>

        <p>
          When backend audit logging is implemented, sensitive administrative
          actions should include the responsible user and timestamp.
        </p>
      </div>
    </section>
  </div>
</template>

<script setup>
import {
  Activity,
  FileClock,
  LogIn,
  Users,
  ShieldCheck,
  UserCheck,
} from '@lucide/vue'
</script>

<style scoped>
.admin-page {
  width: 100%;
}

.page-header {
  margin-bottom: 30px;
}

.eyebrow {
  margin: 0 0 8px;

  color: #2563eb;

  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1.2px;
}

.page-title {
  margin: 0;

  color: #172033;

  font-size: 38px;
  font-weight: 700;
  letter-spacing: -1px;
}

.page-subtitle {
  margin: 9px 0 0;

  color: #64748b;

  font-size: 17px;
}

.status-card {
  display: flex;
  align-items: center;

  gap: 16px;

  margin-bottom: 24px;

  padding: 22px;

  background: #eff6ff;

  border: 1px solid #dbeafe;
  border-radius: 14px;
}

.status-icon {
  width: 50px;
  height: 50px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 12px;

  background: #ffffff;
  color: #2563eb;
}

.status-card h2 {
  margin: 0;

  color: #172033;

  font-size: 18px;
}

.status-card p {
  margin: 5px 0 0;

  color: #64748b;

  font-size: 14px;
  line-height: 1.5;
}

.panel {
  overflow: hidden;

  margin-bottom: 24px;

  background: #ffffff;

  border: 1px solid #e2e8f0;
  border-radius: 14px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 22px;

  border-bottom: 1px solid #e2e8f0;
}

.panel-header h2 {
  margin: 0;

  color: #172033;

  font-size: 18px;
}

.panel-header p {
  margin: 5px 0 0;

  color: #64748b;

  font-size: 14px;
}

.panel-header > svg {
  color: #94a3b8;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;

  padding: 55px 25px;

  text-align: center;
}

.empty-icon {
  width: 58px;
  height: 58px;

  display: flex;
  align-items: center;
  justify-content: center;

  margin-bottom: 16px;

  border-radius: 15px;

  background: #f1f5f9;
  color: #64748b;
}

.empty-state h3 {
  margin: 0;

  color: #172033;

  font-size: 17px;
}

.empty-state p {
  max-width: 500px;

  margin: 8px 0 0;

  color: #64748b;

  font-size: 14px;
  line-height: 1.6;
}

.event-types {
  margin-bottom: 24px;
}

.event-types > h2 {
  margin: 0 0 15px;

  color: #172033;

  font-size: 20px;
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);

  gap: 14px;
}

.event-card {
  display: flex;
  align-items: flex-start;

  gap: 13px;

  padding: 19px;

  background: #ffffff;

  border: 1px solid #e2e8f0;
  border-radius: 12px;
}

.event-card > svg {
  flex-shrink: 0;

  color: #2563eb;
}

.event-card strong {
  color: #172033;

  font-size: 14px;
}

.event-card p {
  margin: 5px 0 0;

  color: #64748b;

  font-size: 13px;
  line-height: 1.5;
}

.info-box {
  display: flex;
  align-items: flex-start;

  gap: 14px;

  padding: 20px 22px;

  background: #f8fafc;

  border: 1px solid #e2e8f0;
  border-radius: 14px;
}

.info-icon {
  width: 42px;
  height: 42px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 10px;

  background: #ffffff;
  color: #2563eb;

  border: 1px solid #e2e8f0;
}

.info-box h3 {
  margin: 0;

  color: #172033;

  font-size: 14px;
}

.info-box p {
  margin: 5px 0 0;

  color: #64748b;

  font-size: 13px;
  line-height: 1.5;
}

@media (max-width: 750px) {
  .event-grid {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: 32px;
  }
}
</style>
