<script setup>
import { onMounted, ref } from 'vue'

import { getDoctorDashboard } from '../services/api'

async function loadDashboard() {
  loading.value = true
  error.value = ''

  try {
    const data = await getDoctorDashboard()

    dashboard.value = {
      doctorName: data.doctorName || '',
      totalPatients: data.totalPatients ?? 0,
      dosesToday: data.dosesToday ?? 0,
      attentionNeeded: data.attentionNeeded ?? 0,
      activity: data.activity || [],
    }
  } catch (err) {
    console.error('Could not load doctor dashboard:', err)

    error.value = 'Dashboard information could not be loaded.'
  } finally {
    loading.value = false
  }
}

function statusClass(status) {
  return String(status || '').toLowerCase()
}

const dashboard = ref({
  doctorName: '',
  totalPatients: 0,
  dosesToday: 0,
  attentionNeeded: 0,
  activity: [],
})

const loading = ref(true)
const error = ref('')

onMounted(loadDashboard)
</script>

<template>
  <section>
    <h1 class="page-title">
      Welcome,
      {{ dashboard.doctorName }}
    </h1>

    <p class="page-subtitle">Here's an overview of your patients and medication activity.</p>

    <p v-if="loading" class="loading-message">Loading dashboard...</p>

    <p v-else-if="error" class="error-message">
      {{ error }}
    </p>

    <template v-else>
      <h2 class="section-title">Patient Overview</h2>

      <div class="summary-grid">
        <div class="summary-card">
          <p class="card-label">Total Patients</p>

          <h3>
            {{ dashboard.totalPatients }}
          </h3>

          <p>Patients currently assigned to your care</p>
        </div>

        <div class="summary-card">
          <p class="card-label">Doses Today</p>

          <h3>
            {{ dashboard.dosesToday }}
          </h3>

          <p>Scheduled medication events across your patients</p>
        </div>

        <div class="summary-card">
          <p class="card-label">Attention Needed</p>

          <h3>
            {{ dashboard.attentionNeeded }}
          </h3>

          <p>Patients requiring review or follow-up</p>
        </div>
      </div>

      <h2 class="section-title">Patient Activity</h2>

      <div v-if="dashboard.activity.length" class="table-card">
        <table>
          <thead>
            <tr>
              <th>Patient</th>
              <th>Medication</th>
              <th>Time</th>
              <th>Status</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="event in dashboard.activity" :key="event.id">
              <td class="patient-name">
                {{ event.patient }}
              </td>

              <td>
                {{ event.medication }}
              </td>

              <td class="bold">
                {{ event.time }}
              </td>

              <td>
                <span class="status-badge" :class="statusClass(event.status)">
                  <span class="status-dot"></span>

                  {{ event.status }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <p v-else class="empty-message">No medication activity is available.</p>
    </template>
  </section>
</template>
