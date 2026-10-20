<template>
  <section class="medications-page">

    <header class="page-header">

      <h1 class="page-title">
        My Medications
      </h1>

      <p class="page-subtitle">
        View your prescribed medications and today's schedule.
      </p>

    </header>


    <div
      v-if="errorMessage"
      class="error-box"
    >
      <CircleAlert :size="20" />

      <div>
        <strong>
          Unable to load medications
        </strong>

        <p>
          {{ errorMessage }}
        </p>
      </div>

      <button @click="loadMedications">
        Retry
      </button>
    </div>


    <div
      v-if="loading"
      class="loading-state"
    >
      <LoaderCircle
        :size="30"
        class="loading-icon"
      />

      Loading medications...
    </div>


    <section
      v-else
      class="medication-section"
    >

      <div class="section-header">

        <div>
          <h2 class="section-title">
            Medication Schedule
          </h2>

          <p class="section-subtitle">
            Your current medications and scheduled doses.
          </p>
        </div>

      </div>


      <div
        v-if="medications.length"
        class="medication-list"
      >

        <RouterLink
          v-for="medication in medications"
          :key="medication.id"
          :to="`/patient/medications/${encodeURIComponent(medication.id)}`"
          class="medication-card"
        >

          <div class="medication-main">

            <div class="medication-icon">
              <Pill :size="22" />
            </div>

            <div class="medication-info">

              <h3>
                {{ medication.name }}
              </h3>

              <p>
                {{ medication.dosage || 'No dosage specified' }}
              </p>

            </div>

          </div>


          <div class="schedule-info">

            <span class="info-label">
              Scheduled time
            </span>

            <strong>
              {{ medication.time || '—' }}
            </strong>

          </div>


          <div class="frequency-info">

            <span class="info-label">
              Frequency
            </span>

            <strong>
              {{ medication.frequency || '—' }}
            </strong>

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


      <div
        v-else-if="!errorMessage"
        class="empty-state"
      >
        <Pill :size="30" />

        <strong>
          No medications available
        </strong>

        <span>
          No prescribed medications were returned for your account.
        </span>
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
  onMounted,
  ref,
} from 'vue'

import {
  Pill,
  CheckCircle2,
  Clock3,
  CalendarClock,
  ChevronRight,
  CircleAlert,
  LoaderCircle,
} from '@lucide/vue'

import {
  getPatientMedications,
} from '../services/api'


const medications = ref([])

const loading = ref(true)
const errorMessage = ref('')


async function loadMedications() {
  loading.value = true
  errorMessage.value = ''

  try {
    const result =
      await getPatientMedications()

    medications.value =
      result.map(addDisplayStatus)

  } catch (error) {
    console.error(
      'Failed to load patient medications:',
      error
    )

    medications.value = []

    errorMessage.value =
      getErrorMessage(error)

  } finally {
    loading.value = false
  }
}


function addDisplayStatus(medication) {
  const raw =
    String(
      medication.status || ''
    ).toLowerCase()

  let status

  if (
    raw === 'taken' ||
    raw === 'completed' ||
    raw === 'collected'
  ) {
    status = 'Taken'

  } else if (raw === 'scheduled') {
    status = 'Scheduled'

  } else if (raw === 'upcoming') {
    status = 'Upcoming'

  } else {
    status = isPastTime(medication.time)
      ? 'Scheduled'
      : 'Upcoming'
  }

  return {
    ...medication,

    status,

    statusClass:
      status.toLowerCase(),
  }
}


function isPastTime(time) {
  if (!time || !time.includes(':')) {
    return false
  }

  const [hours, minutes] =
    time.split(':').map(Number)

  const now = new Date()

  return (
    hours * 60 + minutes <=
    now.getHours() * 60 +
    now.getMinutes()
  )
}


function getErrorMessage(error) {
  if (error?.status === 401) {
    return 'Authentication is required.'
  }

  if (error?.status === 403) {
    return 'You do not have permission to view medications.'
  }

  return 'Medication information is currently unavailable.'
}


onMounted(loadMedications)
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
}

.section-subtitle {
  margin: 6px 0 0;
  color: #64748b;
}

.medication-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.medication-card {
  display: grid;
  grid-template-columns:
    minmax(260px, 1.5fr)
    150px
    130px
    125px
    24px;

  align-items: center;
  gap: 20px;

  padding: 21px 22px;

  background: #ffffff;
  border: 1px solid #e0e6ee;
  border-radius: 16px;

  color: inherit;
  text-decoration: none;
}

.medication-card:hover {
  border-color: #cbd8eb;
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
  border-radius: 12px;
  background: #eaf2ff;
  color: #2563eb;
}

.medication-info h3 {
  margin: 0;
  color: #172033;
  font-size: 17px;
}

.medication-info p {
  margin: 5px 0 0;
  color: #64748b;
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
}

.taken {
  background: #ecfdf3;
  color: #15803d;
}

.scheduled {
  background: #eaf2ff;
  color: #2563eb;
}

.upcoming {
  background: #f1f5f9;
  color: #64748b;
}

.chevron {
  color: #94a3b8;
}

.info-bar {
  display: flex;
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
  line-height: 1.5;
}

.error-box {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding: 15px 17px;
  border: 1px solid #fecaca;
  border-radius: 12px;
  background: #fef2f2;
  color: #b91c1c;
}

.error-box div {
  flex: 1;
}

.error-box p {
  margin: 3px 0 0;
  font-size: 13px;
}

.error-box button {
  padding: 7px 12px;
  border: 1px solid #fecaca;
  border-radius: 7px;
  background: white;
  color: #b91c1c;
  cursor: pointer;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 50px 20px;
  color: #94a3b8;
  text-align: center;
}

.empty-state strong {
  color: #475569;
}

.loading-icon {
  animation: spin .9s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 850px) {
  .medication-card {
    grid-template-columns: 1fr auto;
  }

  .schedule-info,
  .frequency-info,
  .status-badge {
    margin-left: 61px;
  }
}
</style>
