<template>
  <section>
    <RouterLink
      to="/patients"
      class="back-link"
    >
      ← Back to Patients
    </RouterLink>

    <h1 class="page-title">
      {{ patient.name }}
    </h1>

    <p class="page-subtitle">
      Patient details and medication management.
    </p>

    <div class="table-card patient-summary-card">
      <table>
        <thead>
          <tr>
            <th>Patient</th>
            <th>Doctor</th>
            <th>Status</th>
            <th>Adherence</th>
          </tr>
        </thead>

        <tbody>
          <tr>
            <td class="patient-name">
              {{ patient.name }}
            </td>

            <td>
              {{ patient.doctorName }}
            </td>

            <td>
              <span
                class="status-badge"
                :class="patientStatusClass(patient.status)"
              >
                <span class="status-dot"></span>
                {{ patient.status }}
              </span>
            </td>

            <td class="bold">
              {{ patient.adherence }}%
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="medication-heading">
      <h2 class="section-title">
        Current Medications
      </h2>

      <button class="primary-button">
        + Add Medication
      </button>
    </div>

    <div class="table-card">
      <table>
        <thead>
          <tr>
            <th>Medication</th>
            <th>Dosage</th>
            <th>Scheduled</th>
            <th>Frequency</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="medication in patient.medications"
            :key="medication.id"
          >
            <td class="patient-name">
              {{ medication.name }}
            </td>

            <td>
              {{ medication.dosage }}
            </td>

            <td class="bold">
              {{ medication.time }}
            </td>

            <td>
              {{ medication.frequency }}
            </td>

            <td>
              <span
                class="status-badge"
                :class="medicationStatusClass(medication.status)"
              >
                <span class="status-dot"></span>
                {{ medication.status }}
              </span>
            </td>

            <td>
              <div class="action-cell">
                <RouterLink
                  :to="`/patients/${patient.id}/medications/${medication.id}`"
                  class="primary-button"
                >
                  Manage
                </RouterLink>

                <button
                  class="secondary-button"
                  @click="removeMedicationFromPatient(medication.id)"
                >
                  Remove
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <h2 class="section-title">
      Recent Medication Activity
    </h2>

    <div class="table-card">
      <table>
        <thead>
          <tr>
            <th>Time</th>
            <th>Medication</th>
            <th>Activity</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="activity in patient.activity"
            :key="activity.id"
          >
            <td class="bold">
              {{ activity.time }}
            </td>

            <td class="patient-name">
              {{ activity.medication }}
            </td>

            <td>
              {{ activity.description }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import {
  getPatient,
  removeMedication,
} from '../services/api'

const route = useRoute()

const demoPatient = {
  id: 1,
  name: 'Patient A',
  doctorName: 'Anvi',
  status: 'Active',
  adherence: 90,

  medications: [
    {
      id: 1,
      name: 'Medication 1',
      dosage: '1 tablet',
      time: '13:00',
      frequency: 'Once daily',
      status: 'Scheduled',
    },
  ],

  activity: [
    {
      id: 1,
      time: '09:00',
      medication: 'Medication 1',
      description: 'Medication taken',
    },
  ],
}

const patient = ref(demoPatient)

async function loadPatient() {
  try {
    patient.value = await getPatient(route.params.patientId)
  } catch {
    // Temporary fallback until backend integration is complete
    patient.value = demoPatient
  }
}

async function removeMedicationFromPatient(medicationId) {
  const confirmed = window.confirm(
    'Remove this medication from the patient schedule?'
  )

  if (!confirmed) {
    return
  }

  try {
    await removeMedication(
      route.params.patientId,
      medicationId
    )

    patient.value.medications =
      patient.value.medications.filter(
        (medication) => medication.id !== medicationId
      )
  } catch {
    alert('Medication could not be removed.')
  }
}

function patientStatusClass(status) {
  return status === 'Attention' ? 'missed' : 'taken'
}

function medicationStatusClass(status) {
  return status?.toLowerCase() ?? ''
}

onMounted(loadPatient)
</script>
