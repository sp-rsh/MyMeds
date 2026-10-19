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

      <button
        class="primary-button"
        @click="showAddMedication = !showAddMedication"
      >
        + Add Medication
      </button>
    </div>

    <div
      v-if="showAddMedication"
      class="medication-form-card"
    >
      <h2>
        Add Medication
      </h2>

      <form @submit.prevent="addMedicationToPatient">
        <div class="form-content">
          <div class="form-group">
            <label>
              Medication Name
            </label>

            <input
              v-model="newMedication.name"
              type="text"
              required
            />
          </div>

          <div class="form-group">
            <label>
              Dosage
            </label>

            <input
              v-model="newMedication.dosage"
              type="text"
              required
            />
          </div>

          <div class="form-group">
            <label>
              Scheduled Time
            </label>

            <input
              v-model="newMedication.time"
              type="time"
              required
            />
          </div>
        </div>

        <div class="form-footer">
          <div class="form-actions">
            <button
              type="submit"
              class="primary-button"
              :disabled="addingMedication"
            >
              {{ addingMedication ? 'Adding...' : 'Add Medication' }}
            </button>

            <button
              type="button"
              class="secondary-button"
              @click="showAddMedication = false"
            >
              Cancel
            </button>
          </div>
        </div>
      </form>
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
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

import {
  addMedication,
  getPatient,
  removeMedication,
} from '../services/api'

const route = useRoute()

const showAddMedication = ref(false)
const addingMedication = ref(false)

const newMedication = reactive({
  name: '',
  dosage: '',
  time: '',
})

const demoPatient = {
  id: 1,
  name: 'Patient A',
  patientusername: 'patientA',
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
    patient.value = demoPatient
  }
}

async function addMedicationToPatient() {
  addingMedication.value = true

  try {
    await addMedication({
      patientusername:
        patient.value.patientusername ??
        patient.value.username ??
        route.params.patientId,
      medicationName: newMedication.name,
      dosage: newMedication.dosage,
      scheduledTime: newMedication.time,
    })

    patient.value.medications.push({
      id: Date.now(),
      name: newMedication.name,
      dosage: newMedication.dosage,
      time: newMedication.time,
      frequency: 'Once daily',
      status: 'Scheduled',
    })

    newMedication.name = ''
    newMedication.dosage = ''
    newMedication.time = ''

    showAddMedication.value = false
  } catch {
    alert('Medication could not be added.')
  } finally {
    addingMedication.value = false
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
