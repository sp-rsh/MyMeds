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

    <div class="table-card">
      <table>
        <thead>
          <tr>
            <th>Patient</th>
            <th>Current Medication</th>
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
              {{ patient.medication }}
            </td>

            <td class="bold">
              {{ patient.nextDose }}
            </td>

            <td class="bold">
              {{ patient.adherence }}%
            </td>

            <td>
              <span
                class="status-badge"
                :class="statusClass(patient.status)"
              >
                <span class="status-dot"></span>
                {{ patient.status }}
              </span>
            </td>

            <td>
              <RouterLink
                :to="`/patients/${patient.id}`"
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

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getDoctorPatients } from '../services/api'

const search = ref('')
const patients = ref([])

const demoPatients = [
  {
    id: 1,
    name: 'Patient A',
    medication: 'Medication 1',
    nextDose: '13:00',
    adherence: 90,
    status: 'Active',
  },
]

const filteredPatients = computed(() => {
  const value = search.value.trim().toLowerCase()

  if (!value) {
    return patients.value
  }

  return patients.value.filter((patient) =>
    patient.name.toLowerCase().includes(value)
  )
})

function statusClass(status) {
  if (status === 'Attention') {
    return 'missed'
  }

  return 'taken'
}

onMounted(async () => {
  try {
    patients.value = await getDoctorPatients()
  } catch {
    //  fallback until patient API is connected
    patients.value = demoPatients
  }
})
</script>
