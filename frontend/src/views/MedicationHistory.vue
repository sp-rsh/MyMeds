<template>
  <section>
    <h1 class="page-title">
      Medication History
    </h1>

    <p class="page-subtitle">
      Recent medication events for your assigned patients.
    </p>

    <h2 class="section-title">
      Recent Events
    </h2>

    <div class="table-card">
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
          <tr
            v-for="event in events"
            :key="event.id"
          >
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
              {{ event.dose }}
            </td>

            <td>
              <span
                class="status-badge"
                :class="statusClass(event.status)"
              >
                <span class="status-dot"></span>
                {{ event.status }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getMedicationHistory } from '../services/api'

const events = ref([])

const demoEvents = [
  {
    id: 1,
    patient: 'Patient A',
    medication: 'Medication 1',
    time: '09:00',
    dose: '1 tablet',
    status: 'Taken',
  },
  {
    id: 2,
    patient: 'Patient A',
    medication: 'Medication 1',
    time: '13:00',
    dose: '1 tablet',
    status: 'Scheduled',
  },
]

function statusClass(status) {
  return status?.toLowerCase() ?? ''
}

onMounted(async () => {
  try {
    events.value = await getMedicationHistory()
  } catch {
    // fallback until logging API is connected
    events.value = demoEvents
  }
})
</script>
