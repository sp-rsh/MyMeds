<script setup>
import { computed } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import {
  Pill,
  ArrowLeft,
  Clock3,
  CalendarDays,
  CheckCircle2,
  Info,
} from '@lucide/vue'

const route = useRoute()

const medications = {
  'vitamin-d': {
    name: 'Vitamin D',
    dosage: '1 tablet',
    time: '08:00',
    frequency: 'Daily',
    status: 'Taken',
    statusClass: 'taken',
  },
  metformin: {
    name: 'Metformin',
    dosage: '2 tablets',
    time: '13:00',
    frequency: 'Daily',
    status: 'Scheduled',
    statusClass: 'scheduled',
  },
  aspirin: {
    name: 'Aspirin',
    dosage: '1 tablet',
    time: '20:00',
    frequency: 'Daily',
    status: 'Upcoming',
    statusClass: 'upcoming',
  },
}

const medication = computed(() => {
  return medications[route.params.id] || medications['vitamin-d']
})
</script>

<template>
  <section class="details-page">
    <div class="back-row">
      <RouterLink
        to="/patient/medications"
        class="back-button"
      >
        <ArrowLeft :size="18" />
        Back to My Medications
      </RouterLink>
    </div>

    <header class="page-header">
      <h1 class="page-title">Medication Details</h1>
      <p class="page-subtitle">
        View the details of your prescribed medication.
      </p>
    </header>

    <article class="details-card">
      <div class="medication-header">
        <div class="large-medication-icon">
          <Pill :size="28" />
        </div>

        <div>
          <p class="eyebrow">MEDICATION</p>
          <h2>{{ medication.name }}</h2>
          <p class="dosage">{{ medication.dosage }}</p>
        </div>

        <span
          class="status-badge"
          :class="medication.statusClass"
        >
          <CheckCircle2
            v-if="medication.status === 'Taken'"
            :size="16"
          />

          <Clock3
            v-else
            :size="16"
          />

          {{ medication.status }}
        </span>
      </div>

      <div class="divider"></div>

      <div class="details-grid">
        <div class="detail-item">
          <div class="detail-icon">
            <Clock3 :size="20" />
          </div>

          <div>
            <span class="detail-label">Scheduled Time</span>
            <strong>{{ medication.time }}</strong>
          </div>
        </div>

        <div class="detail-item">
          <div class="detail-icon">
            <CalendarDays :size="20" />
          </div>

          <div>
            <span class="detail-label">Frequency</span>
            <strong>{{ medication.frequency }}</strong>
          </div>
        </div>

        <div class="detail-item">
          <div class="detail-icon">
            <Pill :size="20" />
          </div>

          <div>
            <span class="detail-label">Dosage</span>
            <strong>{{ medication.dosage }}</strong>
          </div>
        </div>

        <div class="detail-item">
          <div class="detail-icon">
            <CheckCircle2 :size="20" />
          </div>

          <div>
            <span class="detail-label">Today's Status</span>
            <strong>{{ medication.status }}</strong>
          </div>
        </div>
      </div>
    </article>

    <div class="info-bar">
      <Info :size="20" />

      <p>
        Your medication schedule is prescribed and managed by your care team.
        If you have questions or believe your medication details need to be
        changed, please contact your doctor.
      </p>
    </div>
  </section>
</template>

<style scoped>
.details-page {
  max-width: 1000px;
}

.back-row {
  margin-bottom: 22px;
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 7px;

  color: #64748b;
  text-decoration: none;

  font-size: 14px;
  font-weight: 600;
}

.back-button:hover {
  color: #2563eb;
}

.page-header {
  margin-bottom: 28px;
}

.details-card {
  padding: 30px;

  background: #ffffff;
  border: 1px solid #e0e6ee;
  border-radius: 18px;
}

.medication-header {
  display: flex;
  align-items: center;
  gap: 17px;
}

.large-medication-icon {
  width: 58px;
  height: 58px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 15px;

  background: #eaf2ff;
  color: #2563eb;
}

.eyebrow {
  margin: 0 0 4px;

  color: #94a3b8;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.medication-header h2 {
  margin: 0;

  color: #172033;
  font-size: 25px;
  font-weight: 700;
}

.dosage {
  margin: 5px 0 0;

  color: #64748b;
  font-size: 15px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;

  margin-left: auto;

  padding: 8px 12px;

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

.divider {
  height: 1px;
  margin: 30px 0;

  background: #e0e6ee;
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;

  gap: 18px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 13px;

  padding: 18px;

  border: 1px solid #e5eaf1;
  border-radius: 13px;

  background: #fafbfc;
}

.detail-icon {
  width: 40px;
  height: 40px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 10px;

  background: #ffffff;
  color: #2563eb;
  border: 1px solid #e0e6ee;
}

.detail-item div:last-child {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  color: #94a3b8;
  font-size: 12px;
}

.detail-item strong {
  color: #172033;
  font-size: 15px;
  font-weight: 600;
}

.info-bar {
  display: flex;
  align-items: flex-start;
  gap: 12px;

  margin-top: 24px;
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

@media (max-width: 700px) {
  .details-grid {
    grid-template-columns: 1fr;
  }

  .medication-header {
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .status-badge {
    margin-left: 0;
  }
}
</style>