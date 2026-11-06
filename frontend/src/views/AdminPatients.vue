<script setup>
import { computed, onMounted, ref } from 'vue'

import { getAdminUsers } from '../services/api'

async function loadPatients() {
  loading.value = true
  error.value = ''

  try {
    patients.value = await getAdminUsers('PATIENT')
  } catch (err) {
    console.error('Could not load patients:', err)

    error.value = 'Patients could not be loaded.'
  } finally {
    loading.value = false
  }
}

function formatLastLogin(value) {
  if (!value) {
    return 'Never'
  }

  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  return date.toLocaleString()
}

const patients = ref([])

const loading = ref(true)
const error = ref('')

const search = ref('')

const filteredPatients = computed(() => {
  const value = search.value.trim().toLowerCase()

  if (!value) {
    return patients.value
  }

  return patients.value.filter(
    (patient) =>
      patient.username?.toLowerCase().includes(value) ||
      patient.email?.toLowerCase().includes(value) ||
      patient.doctor?.toLowerCase().includes(value),
  )
})

onMounted(loadPatients)
</script>

<template>
  <section>
    <h1 class="page-title">Patients</h1>

    <p class="page-subtitle">View patient accounts and their assigned doctors.</p>

    <div class="patients-toolbar">
      <h2 class="section-title">Patient List</h2>

      <input v-model="search" class="search-input" type="text" placeholder="Search patients..." />
    </div>

    <p v-if="loading" class="loading-message">Loading patients...</p>

    <p v-else-if="error" class="error-message">
      {{ error }}
    </p>

    <div v-else class="table-card">
      <table>
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Doctor</th>
            <th>Medications</th>
            <th>Status</th>
            <th>Last Login</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="patient in filteredPatients" :key="patient.id">
            <td class="patient-name">
              {{ patient.username }}
            </td>

            <td>
              {{ patient.email }}
            </td>

            <td>
              {{ patient.doctor || 'Not assigned' }}
            </td>

            <td class="bold">
              {{ patient.medications ?? 0 }}
            </td>

            <td>
              <span class="status-badge" :class="patient.active ? 'taken' : 'missed'">
                <span class="status-dot"></span>

                {{ patient.active ? 'Active' : 'Inactive' }}
              </span>
            </td>

            <td>
              {{ formatLastLogin(patient.lastLoginAt) }}
            </td>
          </tr>

          <tr v-if="filteredPatients.length === 0">
            <td colspan="6">No patients found.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
