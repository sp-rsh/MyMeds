<script setup>
import {
  computed,
  onMounted,
  ref,
} from 'vue'

import {
  getDoctorPatients,
} from '../services/api'

async function loadPatients() {
  loading.value = true
  error.value = ''

  try {
    patients.value =
      await getDoctorPatients()
  } catch (err) {
    console.error(
      'Could not load patients:',
      err
    )

    error.value =
      'Patients could not be loaded.'
  } finally {
    loading.value = false
  }
}

function statusClass(status) {
  if (status === 'Attention') {
    return 'missed'
  }

  return 'taken'
}

const search = ref('')
const patients = ref([])

const loading = ref(true)
const error = ref('')

const filteredPatients = computed(() => {
  const value =
    search.value
      .trim()
      .toLowerCase()

  if (!value) {
    return patients.value
  }

  return patients.value.filter(
    patient =>
      patient.name
        ?.toLowerCase()
        .includes(value)
  )
})

onMounted(loadPatients)
</script>

<template>
  <section>
    <h1 class="page-title">
      Patients
    </h1>

    <p class="page-subtitle">
      View and manage your assigned patients.
    </p>

    <div class="patients-toolbar">
      <h2 class="section-title">
        Patient Overview
      </h2>

      <input
        v-model="search"
        class="search-input"
        type="text"
        placeholder="Search patients..."
      />
    </div>

    <p
      v-if="loading"
      class="loading-message"
    >
      Loading patients...
    </p>

    <p
      v-else-if="error"
      class="error-message"
    >
      {{ error }}
    </p>

    <div
      v-else
      class="table-card"
    >
      <table>
        <thead>
          <tr>
            <th>Patient</th>
            <th>Next Medication</th>
            <th>Next Dose</th>
            <th>Adherence</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="patient in filteredPatients"
            :key="patient.id"
          >
            <td class="patient-name">
              {{ patient.name }}
            </td>

            <td>
              {{
                patient.medication ||
                'No medication'
              }}
            </td>

            <td class="bold">
              {{
                patient.nextDose ||
                '—'
              }}
            </td>

            <td class="bold">
              {{ patient.adherence }}%
            </td>

            <td>
              <span
                class="status-badge"
                :class="
                  statusClass(
                    patient.status
                  )
                "
              >
                <span class="status-dot"></span>

                {{ patient.status }}
              </span>
            </td>

            <td>
              <RouterLink
                :to="`/doctor/patients/${patient.id}`"
                class="primary-button"
              >
                View Patient
              </RouterLink>
            </td>
          </tr>

          <tr v-if="filteredPatients.length === 0">
            <td colspan="6">
              No patients found.
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
