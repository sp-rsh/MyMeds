<script setup>
import {
  computed,
  onMounted,
  ref,
} from 'vue'

import {
  Pill,
  Clock3,
  CheckCircle2,
  CalendarClock,
  CircleAlert,
  LoaderCircle,
} from '@lucide/vue'

import {
  getCurrentUser,
  getPatientMedications,
  getPatientLogs,
} from '../services/api'


const user = ref(null)
const medications = ref([])
const logs = ref([])

const loading = ref(true)
const errorMessage = ref('')


async function loadDashboard() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [
      currentUser,
      medicationData,
      logData,
    ] = await Promise.all([
      getCurrentUser(),
      getPatientMedications(),
      getPatientLogs(),
    ])

    user.value = currentUser

    medications.value =
      medicationData.map(addDisplayStatus)

    logs.value =
      getArrayResult(logData)

  } catch (error) {
    console.error(
      'Failed to load patient dashboard:',
      error
    )

    medications.value = []
    logs.value = []

    errorMessage.value =
      getErrorMessage(error)

  } finally {
    loading.value = false
  }
}


function addDisplayStatus(medication) {
  let status =
    normaliseStatus(medication.status)

  if (!status) {
    status =
      isPastTime(medication.time)
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


function normaliseStatus(status) {
  const value =
    String(status || '')
      .toLowerCase()

  if (
    value === 'taken' ||
    value === 'completed' ||
    value === 'collected'
  ) {
    return 'Taken'
  }

  if (value === 'scheduled') {
    return 'Scheduled'
  }

  if (value === 'upcoming') {
    return 'Upcoming'
  }

  return ''
}


function isPastTime(time) {
  if (!time || !time.includes(':')) {
    return false
  }

  const [hours, minutes] =
    time.split(':').map(Number)

  const now = new Date()

  const currentMinutes =
    now.getHours() * 60 +
    now.getMinutes()

  return (
    hours * 60 + minutes <=
    currentMinutes
  )
}


function getArrayResult(result) {
  if (Array.isArray(result)) {
    return result
  }

  if (Array.isArray(result?.content)) {
    return result.content
  }

  if (Array.isArray(result?.logs)) {
    return result.logs
  }

  return []
}


const firstName = computed(() => {
  const fullName =
    user.value?.name ??
    user.value?.fullName ??
    user.value?.username ??
    ''

  return fullName
    .trim()
    .split(/\s+/)[0]
})


const greeting = computed(() => {
  const hour =
    new Date().getHours()

  if (hour < 12) {
    return 'Good morning'
  }

  if (hour < 18) {
    return 'Good afternoon'
  }

  return 'Good evening'
})


const nextDose = computed(() => {
  const available =
    medications.value.filter(
      medication =>
        medication.status !== 'Taken'
    )

  if (!available.length) {
    return null
  }

  return [...available]
    .sort((a, b) =>
      String(a.time)
        .localeCompare(String(b.time))
    )[0]
})


const completedCount = computed(() =>
  medications.value.filter(
    medication =>
      medication.status === 'Taken'
  ).length
)


const adherencePercentage = computed(() => {
  if (!medications.value.length) {
    return 0
  }

  return Math.round(
    completedCount.value /
    medications.value.length *
    100
  )
})


function getErrorMessage(error) {
  if (error?.status === 401) {
    return 'Authentication is required.'
  }

  if (error?.status === 403) {
    return 'You do not have permission to view this information.'
  }

  return 'Patient information is currently unavailable.'
}


onMounted(loadDashboard)
</script>


<template>
  <section class="dashboard">

    <header class="page-header">
      <div>

        <h1 class="page-title">
          {{ greeting }}<template v-if="firstName">, {{ firstName }}</template>
        </h1>

        <p class="page-subtitle">
          Here's an overview of your medication schedule for today.
        </p>

      </div>
    </header>


    <div
      v-if="errorMessage"
      class="error-box"
    >
      <CircleAlert :size="20" />

      <div>
        <strong>
          Unable to load dashboard
        </strong>

        <p>
          {{ errorMessage }}
        </p>
      </div>

      <button
        type="button"
        @click="loadDashboard"
      >
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

      Loading your medication schedule...
    </div>


    <template v-else>

      <div class="top-grid">

        <article class="next-dose-card">

          <div class="card-heading">

            <div class="icon-box blue-icon">
              <Pill :size="22" />
            </div>

            <div>
              <p class="eyebrow">
                NEXT DOSE
              </p>

              <h2>
                {{
                  nextDose
                    ? nextDose.name
                    : 'No remaining doses'
                }}
              </h2>
            </div>

          </div>


          <div
            v-if="nextDose"
            class="next-dose-content"
          >

            <div>
              <p class="dose-time">
                {{ nextDose.time || '—' }}
              </p>

              <p class="dose-countdown">
                {{ nextDose.dosage }}
              </p>
            </div>


            <span
              class="status-badge"
              :class="nextDose.statusClass"
            >
              <Clock3 :size="15" />

              {{ nextDose.status }}
            </span>

          </div>

        </article>


        <article class="adherence-card">

          <div class="card-heading">

            <div class="icon-box green-icon">
              <CheckCircle2 :size="22" />
            </div>

            <div>
              <p class="eyebrow">
                TODAY'S ADHERENCE
              </p>

              <h2>
                Medication Progress
              </h2>
            </div>

          </div>


          <div class="adherence-content">

            <div class="progress-circle">
              <span>
                {{ adherencePercentage }}%
              </span>
            </div>

            <div class="adherence-text">

              <strong>
                {{ completedCount }} of {{ medications.length }}
              </strong>

              <span>
                medications completed
              </span>

            </div>

          </div>

        </article>

      </div>


      <section class="medication-section">

        <div class="section-header">

          <div>
            <h2 class="section-title">
              Today's Medication
            </h2>

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


        <div
          v-if="medications.length"
          class="medication-list"
        >

          <article
            v-for="medication in medications"
            :key="medication.id"
            class="medication-card"
          >

            <div class="medication-main">

              <div class="medication-icon">
                <Pill :size="21" />
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


            <div class="medication-time">

              <span class="time-label">
                Scheduled time
              </span>

              <strong>
                {{ medication.time || '—' }}
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

          </article>

        </div>


        <div
          v-else-if="!errorMessage"
          class="empty-state"
        >
          <Pill :size="28" />

          <strong>
            No medications scheduled
          </strong>

          <span>
            No medication schedule was returned for your account.
          </span>
        </div>

      </section>


      <div class="info-bar">

        <Pill :size="20" />

        <p>
          Your medication schedule is prescribed and managed by your care team.
          If you have questions about your medication, please contact your doctor.
        </p>

      </div>

    </template>

  </section>
</template>


<style scoped>
.dashboard {
  max-width: 1200px;
}

.page-header {
  margin-bottom: 32px;
}

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
}

.dose-countdown {
  margin: 5px 0 0;
  color: #64748b;
}

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
}

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
}

.section-subtitle {
  margin: 6px 0 0;
  color: #64748b;
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
}

.medication-info p {
  margin: 4px 0 0;
  color: #64748b;
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

.error-box strong {
  display: block;
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

@media (max-width: 900px) {
  .top-grid {
    grid-template-columns: 1fr;
  }

  .medication-card {
    grid-template-columns: 1fr;
  }
}
</style>
