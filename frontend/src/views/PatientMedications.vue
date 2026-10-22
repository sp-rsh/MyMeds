<script setup>
import { onMounted, ref } from 'vue'

import { getPatientMedications } from '../services/api'

async function loadMedications() {
  loading.value = true
  error.value = ''

  try {
    medications.value = await getPatientMedications()
  } catch (err) {
    console.error('Could not load medications:', err)

    error.value = 'Your medications could not be loaded.'
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

const medications = ref([])

const loading = ref(true)
const error = ref('')

onMounted(loadMedications)
</script>

<template>
  <section>
    <h1 class="page-title">My Medications</h1>

    <p class="page-subtitle">Your current prescribed medications.</p>

    <div v-if="loading">
      <p class="page-subtitle">Loading medications...</p>
    </div>

    <div v-else-if="error" class="success-message">
      {{ error }}
    </div>

    <template v-else>
      <h2 class="section-title">Medication Schedule</h2>

      <div v-if="medications.length" class="table-card">
        <table>
          <thead>
            <tr>
              <th>Medication</th>
              <th>Dosage</th>
              <th>Tower To Dispense</th>
              <th>Time</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="medication in medications" :key="medication.id">
              <td class="patient-name">
                {{ medication.medicationName }}
              </td>

              <td>
                {{ medication.dosage }}
              </td>

              <td>
                {{ medication.towerToDispense }}
              </td>

              <td class="bold">
                {{ formatTime(medication.scheduledTime) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else class="success-message">You currently have no prescribed medications.</div>
    </template>
  </section>
</template>
