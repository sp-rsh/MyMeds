<script setup>
import { computed, onMounted, ref } from 'vue'

import { getSecurityLogs } from '../services/api'

async function loadSecurityLogs() {
  loading.value = true
  error.value = ''

  try {
    logs.value = await getSecurityLogs()
  } catch (err) {
    console.error('Could not load security logs:', err)

    error.value = 'Security logs could not be loaded.'
  } finally {
    loading.value = false
  }
}

function formatTime(value) {
  if (!value) {
    return '—'
  }

  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  return date.toLocaleString()
}

const logs = ref([])

const loading = ref(true)
const error = ref('')

const search = ref('')

const filteredLogs = computed(() => {
  const value = search.value.trim().toLowerCase()

  if (!value) {
    return logs.value
  }

  return logs.value.filter(
    (log) =>
      log.event?.toLowerCase().includes(value) ||
      log.user?.toLowerCase().includes(value) ||
      log.type?.toLowerCase().includes(value) ||
      log.result?.toLowerCase().includes(value) ||
      log.details?.toLowerCase().includes(value),
  )
})

onMounted(loadSecurityLogs)
</script>

<template>
  <section>
    <h1 class="page-title">Security Logs</h1>

    <p class="page-subtitle">Review recorded security and account activity.</p>

    <div class="patients-toolbar">
      <h2 class="section-title">Activity Log</h2>

      <input
        v-model="search"
        class="search-input"
        type="text"
        placeholder="Search security logs..."
      />
    </div>

    <p v-if="loading" class="loading-message">Loading security logs...</p>

    <p v-else-if="error" class="error-message">
      {{ error }}
    </p>

    <div v-else class="table-card">
      <table>
        <thead>
          <tr>
            <th>Time</th>
            <th>Event</th>
            <th>User</th>
            <th>Type</th>
            <th>Result</th>
            <th>Details</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="log in filteredLogs" :key="log.id">
            <td>
              {{ formatTime(log.time) }}
            </td>

            <td class="patient-name">
              {{ log.event }}
            </td>

            <td>
              {{ log.user || '—' }}
            </td>

            <td>
              {{ log.type || '—' }}
            </td>

            <td>
              <span
                class="status-badge"
                :class="String(log.result).toLowerCase().includes('success') ? 'taken' : 'missed'"
              >
                <span class="status-dot"></span>

                {{ log.result || 'Unknown' }}
              </span>
            </td>

            <td>
              {{ log.details || '—' }}
            </td>
          </tr>

          <tr v-if="filteredLogs.length === 0">
            <td colspan="6">No security logs found.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
