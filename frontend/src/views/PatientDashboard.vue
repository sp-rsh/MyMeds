<script setup>
import { computed, onMounted, ref } from 'vue'

import { getCurrentUser, getPatientAlerts, getPatientMedications } from '../services/api'

async function loadDashboard() {
  loading.value = true
  error.value = ''

  try {
    const [userData, medicationData, alertData] = await Promise.all([
      getCurrentUser(),
      getPatientMedications(),
      getPatientAlerts(),
    ])

    user.value = userData
    medications.value = medicationData
    alerts.value = alertData
  } catch (err) {
    console.error('Could not load patient dashboard:', err)

    error.value = 'Your medication information could not be loaded.'
  } finally {
    loading.value = false
  }
}

function formatTime(value) {
  if (!value) {
    return '—'
  }

  return String(value).slice(0, 5)
}

function timeInMinutes(value) {
  if (!value) {
    return Number.MAX_SAFE_INTEGER
  }

  const [hours, minutes] = String(value).slice(0, 5).split(':').map(Number)

  return hours * 60 + minutes
}

const user = ref(null)
const medications = ref([])
const alerts = ref([])

const loading = ref(true)
const error = ref('')

const greeting = computed(() => {
  const hour = new Date().getHours()

  if (hour < 12) {
    return 'Good morning'
  }

  if (hour < 18) {
    return 'Good afternoon'
  }

  return 'Good evening'
})

const patientName = computed(() => {
  return user.value?.name ?? user.value?.username ?? ''
})

const nextMedication = computed(() => {
  const now = new Date()

  const currentMinutes = now.getHours() * 60 + now.getMinutes()

  const upcoming = medications.value
    .filter((medication) => timeInMinutes(medication.scheduledTime) >= currentMinutes)
    .sort((a, b) => timeInMinutes(a.scheduledTime) - timeInMinutes(b.scheduledTime))

  return upcoming[0] ?? null
})

onMounted(loadDashboard)
</script>

<template>
  <section>
    <h1 class="page-title">
      {{ greeting }},
      {{ patientName }}
    </h1>

    <p class="page-subtitle">Here is your medication information for today.</p>

    <div v-if="loading">
      <p class="page-subtitle">Loading your medications...</p>
    </div>

    <div v-else-if="error" class="success-message">
      {{ error }}
    </div>

    <template v-else>
      <h2 class="section-title">Today</h2>

      <div class="summary-grid">
        <div class="summary-card">
          <p class="card-label">My Medications</p>

          <h3>
            {{ medications.length }}
          </h3>

          <p>Prescribed medications</p>
        </div>

        <div class="summary-card">
          <p class="card-label">Missed Doses</p>

          <h3>
            {{ alerts.length }}
          </h3>

          <p>Doses that may need your attention</p>
        </div>

        <div class="summary-card">
          <p class="card-label">Next Dose</p>

          <h3>
            {{ nextMedication ? formatTime(nextMedication.scheduledTime) : '—' }}
          </h3>

          <p>
            {{ nextMedication ? nextMedication.name : 'No more scheduled doses today' }}
          </p>
        </div>
      </div>

      <template v-if="alerts.length">
        <h2 class="section-title">Needs Attention</h2>

        <div class="table-card">
          <table>
            <thead>
              <tr>
                <th>Medication</th>
                <th>Scheduled Time</th>
                <th>Message</th>
              </tr>
            </thead>

            <tbody>
              <tr v-for="alert in alerts" :key="alert.medicationId">
                <td class="patient-name">
                  {{ alert.medicationName }}
                </td>

                <td class="bold">
                  {{ formatTime(alert.scheduledTime) }}
                </td>

                <td>
                  {{ alert.message }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </template>

      <div v-else class="success-message">No missed medication doses require your attention.</div>
    </template>
  </section>
</template>
