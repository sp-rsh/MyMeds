<script setup>
import { onMounted, ref } from 'vue'

import { getMedicationHistory } from '../services/api'

async function loadHistory() {
  loading.value = true
  error.value = ''

  try {
    events.value = await getMedicationHistory()
  } catch (err) {
    console.error('Could not load medication history:', err)

    error.value = 'Medication history could not be loaded.'
  } finally {
    loading.value = false
  }
}

function statusClass(status) {
  return String(status || '').toLowerCase()
}

const events = ref([])

const loading = ref(true)
const error = ref('')

onMounted(loadHistory)
</script>

<template>
  <section>
    <h1 class="page-title">Medication History</h1>

    <p class="page-subtitle">Recent medication events for your assigned patients.</p>

    <h2 class="section-title">Recent Events</h2>

    <p v-if="loading" class="loading-message">Loading medication history...</p>

    <p v-else-if="error" class="error-message">
      {{ error }}
    </p>

    <div v-else-if="events.length" class="table-card">
      <table>
        <thead>
          <tr>
            <th>Patient</th>
            <th>Medication</th>
            <th>Time</th>
            <th>Dose</th>
            <th>Status</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="event in events" :key="event.id">
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
              {{ event.dose || '—' }}
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

    <p v-else class="empty-message">No medication history is available.</p>
  </section>
</template>
