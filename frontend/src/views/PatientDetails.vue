<script setup>
import {
  onMounted,
  reactive,
  ref,
} from 'vue'

import {
  useRoute,
} from 'vue-router'

import {
  addMedication,
  getPatient,
  removeMedication,
} from '../services/api'

async function loadPatient() {
  loading.value = true
  error.value = ''

  try {
    patient.value =
      await getPatient(
        route.params.patientId
      )
  } catch (err) {
    console.error(
      'Could not load patient:',
      err
    )

    error.value =
      'Patient information could not be loaded.'
  } finally {
    loading.value = false
  }
}

function openAddMedication() {
  newMedication.name = ''
  newMedication.dosage = ''
  newMedication.time = ''
  newMedication.frequency =
    'Once daily'

  medicationError.value = ''
  showAddMedication.value = true
}

function closeAddMedication() {
  showAddMedication.value = false
  medicationError.value = ''
}

async function addMedicationToPatient() {
  addingMedication.value = true
  medicationError.value = ''

  try {
    await addMedication({
      patientusername:
        patient.value.patientusername,
      medicationName:
        newMedication.name,
      dosage:
        newMedication.dosage,
      scheduledTime:
        newMedication.time,
      frequency:
        newMedication.frequency,
    })

    closeAddMedication()

    await loadPatient()
  } catch (err) {
    console.error(
      'Could not add medication:',
      err
    )

    medicationError.value =
      err.message ||
      'Medication could not be added.'
  } finally {
    addingMedication.value = false
  }
}

async function removeMedicationFromPatient(
  medicationId
) {
  const confirmed =
    window.confirm(
      'Remove this medication from the patient schedule?'
    )

  if (!confirmed) {
    return
  }

  medicationError.value = ''

  try {
    await removeMedication(
      route.params.patientId,
      medicationId
    )

    await loadPatient()
  } catch (err) {
    console.error(
      'Could not remove medication:',
      err
    )

    medicationError.value =
      err.message ||
      'Medication could not be removed.'
  }
}

function patientStatusClass(status) {
  if (status === 'Attention') {
    return 'missed'
  }

  return 'taken'
}

function medicationStatusClass(status) {
  return String(
    status || ''
  ).toLowerCase()
}

const route = useRoute()

const patient = ref(null)

const loading = ref(true)
const error = ref('')

const showAddMedication = ref(false)
const addingMedication = ref(false)
const medicationError = ref('')

const newMedication = reactive({
  name: '',
  dosage: '',
  time: '',
  frequency: 'Once daily',
})

onMounted(loadPatient)
</script>

<template>
  <section>
    <RouterLink
      to="/doctor/patients"
      class="back-link"
    >
      ← Back to Patients
    </RouterLink>

    <h1 class="page-title">
      {{
        patient?.name ||
        'Patient'
      }}
    </h1>

    <p class="page-subtitle">
      Patient details and medication management.
    </p>

    <p
      v-if="loading"
      class="loading-message"
    >
      Loading patient...
    </p>

    <p
      v-else-if="error"
      class="error-message"
    >
      {{ error }}
    </p>

    <template v-else-if="patient">
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
                  :class="
                    patientStatusClass(
                      patient.status
                    )
                  "
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
          type="button"
          class="primary-button"
          @click="openAddMedication"
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

        <form
          @submit.prevent="
            addMedicationToPatient
          "
        >
          <div class="form-content">
            <div class="form-group">
              <label for="new-medication-name">
                Medication Name
              </label>

              <input
                id="new-medication-name"
                v-model="newMedication.name"
                type="text"
                required
              />
            </div>

            <div class="form-group">
              <label for="new-medication-dosage">
                Dosage
              </label>

              <input
                id="new-medication-dosage"
                v-model="newMedication.dosage"
                type="text"
                required
              />
            </div>

            <div class="form-group">
              <label for="new-medication-time">
                Scheduled Time
              </label>

              <input
                id="new-medication-time"
                v-model="newMedication.time"
                type="time"
                required
              />
            </div>

            <div class="form-group">
              <label for="new-medication-frequency">
                Frequency
              </label>

              <select
                id="new-medication-frequency"
                v-model="newMedication.frequency"
                required
              >
                <option value="Once daily">
                  Once daily
                </option>

                <option value="Twice daily">
                  Twice daily
                </option>

                <option value="Three times daily">
                  Three times daily
                </option>
              </select>
            </div>

            <p
              v-if="medicationError"
              class="error-message"
            >
              {{ medicationError }}
            </p>
          </div>

          <div class="form-footer">
            <div class="form-actions">
              <button
                type="submit"
                class="primary-button"
                :disabled="addingMedication"
              >
                {{
                  addingMedication
                    ? 'Adding...'
                    : 'Add Medication'
                }}
              </button>

              <button
                type="button"
                class="secondary-button"
                @click="closeAddMedication"
              >
                Cancel
              </button>
            </div>
          </div>
        </form>
      </div>

      <p
        v-if="medicationError && !showAddMedication"
        class="error-message"
      >
        {{ medicationError }}
      </p>

      <div
        v-if="patient.medications.length"
        class="table-card"
      >
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
                  :class="
                    medicationStatusClass(
                      medication.status
                    )
                  "
                >
                  <span class="status-dot"></span>

                  {{ medication.status }}
                </span>
              </td>

              <td>
                <div class="action-cell">
                  <RouterLink
                    :to="`/doctor/patients/${patient.id}/medications/${medication.id}`"
                    class="primary-button"
                  >
                    Manage
                  </RouterLink>

                  <button
                    type="button"
                    class="secondary-button"
                    @click="
                      removeMedicationFromPatient(
                        medication.id
                      )
                    "
                  >
                    Remove
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <p
        v-else
        class="empty-message"
      >
        This patient currently has no medications.
      </p>

      <h2 class="section-title">
        Recent Medication Activity
      </h2>

      <div
        v-if="patient.activity.length"
        class="table-card"
      >
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

      <p
        v-else
        class="empty-message"
      >
        No medication activity is available.
      </p>
    </template>
  </section>
</template>
