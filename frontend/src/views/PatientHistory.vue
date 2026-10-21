<script setup>
import {
  onMounted,
  ref,
} from 'vue'

import {
  getPatientLogs,
} from '../services/api'


async function loadHistory() {
  loading.value = true
  error.value = ''

  try {
    history.value =
      await getPatientLogs()

  } catch (err) {
    console.error(
      'Could not load medication history:',
      err
    )

    error.value =
      'Your medication history could not be loaded.'

  } finally {
    loading.value = false
  }
}


function formatDate(value) {
  if (!value) {
    return '—'
  }

  const date =
    new Date(value)

  if (
    Number.isNaN(
      date.getTime()
    )
  ) {
    return value
  }

  return date.toLocaleDateString()
}


function formatTime(value) {
  if (!value) {
    return '—'
  }

  const date =
    new Date(value)

  if (
    Number.isNaN(
      date.getTime()
    )
  ) {
    return '—'
  }

  return date.toLocaleTimeString(
    [],
    {
      hour: '2-digit',
      minute: '2-digit',
    }
  )
}


function statusClass(eventType) {
  const type =
    String(
      eventType ?? ''
    ).toUpperCase()

  if (
    type.includes('DISPENS') ||
    type.includes('TAKEN') ||
    type.includes('COLLECT')
  ) {
    return 'taken'
  }

  if (
    type.includes('MISS')
  ) {
    return 'missed'
  }

  return 'scheduled'
}


const history = ref([])

const loading = ref(true)
const error = ref('')


onMounted(loadHistory)
</script>


<template>
  <section>

    <h1 class="page-title">
      Medication History
    </h1>


    <p class="page-subtitle">
      Your recorded medication activity.
    </p>


    <div v-if="loading">
      <p class="page-subtitle">
        Loading medication history...
      </p>
    </div>


    <div
      v-else-if="error"
      class="success-message"
    >
      {{ error }}
    </div>


    <template v-else>

      <h2 class="section-title">
        Recent Activity
      </h2>


      <div
        v-if="history.length"
        class="table-card"
      >

        <table>

          <thead>
            <tr>
              <th>Date</th>
              <th>Time</th>
              <th>Medication</th>
              <th>Event</th>
              <th>Details</th>
            </tr>
          </thead>


          <tbody>

            <tr
              v-for="event in history"
              :key="event.id"
            >

              <td>
                {{
                  formatDate(
                    event.eventTime
                  )
                }}
              </td>


              <td class="bold">
                {{
                  formatTime(
                    event.eventTime
                  )
                }}
              </td>


              <td class="patient-name">
                {{ event.medicationName }}
              </td>


              <td>

                <span
                  class="status-badge"
                  :class="
                    statusClass(
                      event.eventType
                    )
                  "
                >
                  <span class="status-dot"></span>

                  {{ event.eventType }}
                </span>

              </td>


              <td>
                {{ event.details || '—' }}
              </td>

            </tr>

          </tbody>

        </table>

      </div>


      <div
        v-else
        class="success-message"
      >
        No medication activity has been recorded yet.
      </div>

    </template>

  </section>
</template>
