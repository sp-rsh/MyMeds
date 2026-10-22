<template>
  <section class="medications-page">
    <header class="page-header">
      <div>
        <h1 class="page-title">My Medications</h1>
        <p class="page-subtitle">
          View your prescribed medications and today's schedule.
        </p>
      </div>
    </header>

    <section class="medication-section">
      <div class="section-header">
        <div>
          <h2 class="section-title">Medication Schedule</h2>
          <p class="section-subtitle">
            Your current medications and scheduled doses.
          </p>
        </div>
      </div>

      <div class="medication-list">
        <RouterLink
          v-for="medication in medications"
          :key="medication.id"
          :to="`/patient/medications/${medication.id}`"
          class="medication-card"
        >
          <div class="medication-main">
            <div class="medication-icon">
              <Pill :size="22" />
            </div>

            <div class="medication-info">
              <h3>{{ medication.name }}</h3>
              <p>{{ medication.dosage }}</p>
            </div>
          </div>

          <div class="schedule-info">
            <span class="info-label">Scheduled time</span>
            <strong>{{ medication.time }}</strong>
          </div>

          <div class="frequency-info">
            <span class="info-label">Frequency</span>
            <strong>{{ medication.frequency }}</strong>
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

          <ChevronRight
            class="chevron"
            :size="20"
          />
        </RouterLink>
      </div>
    </section>

    <div class="info-bar">
      <Pill :size="20" />

      <p>
        Your medication schedule is prescribed and managed by your care team.
        You can view medication details, but medication changes must be made
        by your doctor.
      </p>
    </div>
  </section>
</template>

<script setup>
import {
  Pill,
  CheckCircle2,
  Clock3,
  CalendarClock,
  ChevronRight,
} from '@lucide/vue'

const medications = [
  {
    id: 'vitamin-d',
    name: 'Vitamin D',
    dosage: '1 tablet',
    time: '08:00',
    frequency: 'Daily',
    status: 'Taken',
    statusClass: 'taken',
  },
  {
    id: 'metformin',
    name: 'Metformin',
    dosage: '2 tablets',
    time: '13:00',
    frequency: 'Daily',
    status: 'Scheduled',
    statusClass: 'scheduled',
  },
  {
    id: 'aspirin',
    name: 'Aspirin',
    dosage: '1 tablet',
    time: '20:00',
    frequency: 'Daily',
    status: 'Upcoming',
    statusClass: 'upcoming',
  },
]
</script>

<style scoped>
.medications-page {
  max-width: 1200px;
}

.page-header {
  margin-bottom: 34px;
}

.medication-section {
  margin-bottom: 28px;
}

.section-header {
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

.medication-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.medication-card {
  display: grid;
  grid-template-columns: minmax(260px, 1.5fr) 150px 130px 125px 24px;
  align-items: center;
  gap: 20px;

  padding: 21px 22px;

  background: #ffffff;
  border: 1px solid #e0e6ee;
  border-radius: 16px;

  color: inherit;
  text-decoration: none;

  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease,
    transform 0.15s ease;
}

.medication-card:hover {
  border-color: #cbd8eb;
  box-shadow: 0 5px 18px rgba(23, 32, 51, 0.06);
  transform: translateY(-1px);
}

.medication-main {
  display: flex;
  align-items: center;
  gap: 15px;
}

.medication-icon {
  width: 46px;
  height: 46px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 12px;

  background: #eaf2ff;
  color: #2563eb;
}

.medication-info h3 {
  margin: 0;

  color: #172033;
  font-size: 17px;
  font-weight: 650;
}

.medication-info p {
  margin: 5px 0 0;

  color: #64748b;
  font-size: 14px;
}

.schedule-info,
.frequency-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-label {
  color: #94a3b8;
  font-size: 12px;
}

.schedule-info strong,
.frequency-info strong {
  color: #172033;
  font-size: 15px;
  font-weight: 600;
}

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

.chevron {
  color: #94a3b8;
}

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

@media (max-width: 1050px) {
  .medication-card {
    grid-template-columns: 1fr 140px 120px 120px 24px;
    gap: 14px;
  }
}

@media (max-width: 850px) {
  .medication-card {
    grid-template-columns: 1fr auto;
    gap: 16px;
  }

  .schedule-info,
  .frequency-info {
    margin-left: 61px;
  }

  .status-badge {
    margin-left: 61px;
  }

  .chevron {
    grid-column: 2;
    grid-row: 1;
  }
}
</style>