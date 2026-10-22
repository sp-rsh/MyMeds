<script setup>
import {
  Pill,
  Clock3,
  CheckCircle2,
  CalendarClock,
} from '@lucide/vue'

const medications = [
  {
    name: 'Vitamin D',
    time: '08:00',
    dosage: '1 tablet',
    status: 'Taken',
    statusClass: 'taken',
  },
  {
    name: 'Metformin',
    time: '13:00',
    dosage: '2 tablets',
    status: 'Scheduled',
    statusClass: 'scheduled',
  },
  {
    name: 'Aspirin',
    time: '20:00',
    dosage: '1 tablet',
    status: 'Upcoming',
    statusClass: 'upcoming',
  },
]
</script>

<template>
  <section class="dashboard">
    <!-- Header -->
    <header class="page-header">
      <div>
        <h1 class="page-title">Good morning, Anvi</h1>
        <p class="page-subtitle">
          Here's an overview of your medication schedule for today.
        </p>
      </div>
    </header>

    <!-- Top cards -->
    <div class="top-grid">
      <!-- Next dose -->
      <article class="next-dose-card">
        <div class="card-heading">
          <div class="icon-box blue-icon">
            <Pill :size="22" />
          </div>

          <div>
            <p class="eyebrow">NEXT DOSE</p>
            <h2>Metformin</h2>
          </div>
        </div>

        <div class="next-dose-content">
          <div>
            <p class="dose-time">13:00</p>
            <p class="dose-countdown">In 1h 20m</p>
          </div>

          <span class="status-badge scheduled">
            <Clock3 :size="15" />
            Scheduled
          </span>
        </div>
      </article>

      <!-- Adherence -->
      <article class="adherence-card">
        <div class="card-heading">
          <div class="icon-box green-icon">
            <CheckCircle2 :size="22" />
          </div>

          <div>
            <p class="eyebrow">TODAY'S ADHERENCE</p>
            <h2>Medication Progress</h2>
          </div>
        </div>

        <div class="adherence-content">
          <div class="progress-circle">
            <span>67%</span>
          </div>

          <div class="adherence-text">
            <strong>2 of 3</strong>
            <span>medications completed</span>
          </div>
        </div>
      </article>
    </div>

    <!-- Today's medication -->
    <section class="medication-section">
      <div class="section-header">
        <div>
          <h2 class="section-title">Today's Medication</h2>
          <p class="section-subtitle">
            Your scheduled medications for today.
          </p>
        </div>

        <RouterLink
          to="/patient/medications"
          class="view-all-button"
        >
          <CalendarClock :size="18" />
          View all
        </RouterLink>
      </div>

      <div class="medication-list">
        <article
          v-for="medication in medications"
          :key="medication.name"
          class="medication-card"
        >
          <div class="medication-main">
            <div class="medication-icon">
              <Pill :size="21" />
            </div>

            <div class="medication-info">
              <h3>{{ medication.name }}</h3>
              <p>{{ medication.dosage }}</p>
            </div>
          </div>

          <div class="medication-time">
            <span class="time-label">Scheduled time</span>
            <strong>{{ medication.time }}</strong>
          </div>

          <span
            class="status-badge"
            :class="medication.statusClass"
          >
            <CheckCircle2
              v-if="medication.status === 'Taken'"
              :size="15"
            />

            <Clock3
              v-else-if="medication.status === 'Scheduled'"
              :size="15"
            />

            <CalendarClock
              v-else
              :size="15"
            />

            {{ medication.status }}
          </span>
        </article>
      </div>
    </section>

    <!-- Information bar -->
    <div class="info-bar">
      <Pill :size="20" />

      <p>
        Your medication schedule is prescribed and managed by your care team.
        If you have questions about your medication, please contact your doctor.
      </p>
    </div>
  </section>
</template>

<style scoped>
.dashboard {
  max-width: 1200px;
}

/* Header */

.page-header {
  margin-bottom: 32px;
}

/* Top cards */

.top-grid {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 24px;
  margin-bottom: 40px;
}

.next-dose-card,
.adherence-card {
  padding: 28px;
  background: #ffffff;
  border: 1px solid #e0e6ee;
  border-radius: 18px;
}

.card-heading {
  display: flex;
  align-items: center;
  gap: 14px;
}

.icon-box {
  width: 46px;
  height: 46px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 13px;
}

.blue-icon {
  background: #eaf2ff;
  color: #2563eb;
}

.green-icon {
  background: #ecfdf3;
  color: #16a34a;
}

.eyebrow {
  margin: 0 0 4px;
  color: #94a3b8;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.card-heading h2 {
  margin: 0;
  color: #172033;
  font-size: 20px;
  font-weight: 700;
}

.next-dose-content {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;

  margin-top: 30px;
}

.dose-time {
  margin: 0;
  color: #172033;
  font-size: 34px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.dose-countdown {
  margin: 5px 0 0;
  color: #64748b;
  font-size: 15px;
}

/* Adherence */

.adherence-content {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-top: 24px;
}

.progress-circle {
  width: 92px;
  height: 92px;

  display: flex;
  align-items: center;
  justify-content: center;

  border: 9px solid #dbeafe;
  border-right-color: #2563eb;
  border-bottom-color: #2563eb;
  border-radius: 50%;
}

.progress-circle span {
  color: #172033;
  font-size: 20px;
  font-weight: 700;
}

.adherence-text {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.adherence-text strong {
  color: #172033;
  font-size: 22px;
}

.adherence-text span {
  color: #64748b;
  font-size: 15px;
}

/* Medication section */

.medication-section {
  margin-bottom: 28px;
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 18px;
}

.section-title {
  margin: 0;
  color: #172033;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.4px;
}

.section-subtitle {
  margin: 6px 0 0;
  color: #64748b;
  font-size: 15px;
}

.view-all-button {
  display: flex;
  align-items: center;
  gap: 7px;

  padding: 9px 13px;

  border: 1px solid #dbe3ee;
  border-radius: 10px;

  background: #ffffff;
  color: #2563eb;

  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
}

.view-all-button:hover {
  background: #f8fafc;
}

/* Medication cards */

.medication-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.medication-card {
  display: grid;
  grid-template-columns: 1fr 180px 130px;
  align-items: center;
  gap: 20px;

  padding: 18px 22px;

  background: #ffffff;
  border: 1px solid #e0e6ee;
  border-radius: 15px;
}

.medication-main {
  display: flex;
  align-items: center;
  gap: 14px;
}

.medication-icon {
  width: 42px;
  height: 42px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 11px;

  background: #f1f5f9;
  color: #475569;
}

.medication-info h3 {
  margin: 0;
  color: #172033;
  font-size: 17px;
  font-weight: 650;
}

.medication-info p {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 14px;
}

.medication-time {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.time-label {
  color: #94a3b8;
  font-size: 12px;
}

.medication-time strong {
  color: #172033;
  font-size: 16px;
}

/* Status */

.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;

  width: fit-content;

  padding: 7px 11px;

  border-radius: 999px;

  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}

.status-badge.taken {
  background: #ecfdf3;
  color: #15803d;
}

.status-badge.scheduled {
  background: #eaf2ff;
  color: #2563eb;
}

.status-badge.upcoming {
  background: #f1f5f9;
  color: #64748b;
}

/* Information bar */

.info-bar {
  display: flex;
  align-items: flex-start;
  gap: 12px;

  padding: 17px 20px;

  border: 1px solid #dbeafe;
  border-radius: 14px;

  background: #f8fbff;
  color: #2563eb;
}

.info-bar p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
  line-height: 1.5;
}

/* Responsive */

@media (max-width: 900px) {
  .top-grid {
    grid-template-columns: 1fr;
  }

  .medication-card {
    grid-template-columns: 1fr;
  }

  .medication-time {
    margin-left: 56px;
  }
}

@media (max-width: 600px) {
  .section-header {
    align-items: flex-start;
    flex-direction: column;
    gap: 12px;
  }

  .next-dose-content {
    align-items: flex-start;
    flex-direction: column;
    gap: 18px;
  }
}
</style>