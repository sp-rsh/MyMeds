<script setup>
import { computed, onMounted, ref } from 'vue'

import {
  getAdminUsers,
  getDoctorAssignedPatients,
  updateDoctorPatientAssignments,
} from '../services/api'

async function loadDoctors() {
  loading.value = true
  error.value = ''

  try {
    doctors.value = await getAdminUsers('DOCTOR')
  } catch (err) {
    console.error('Could not load doctors:', err)

    error.value = 'Doctors could not be loaded.'
  } finally {
    loading.value = false
  }
}

async function openPatientAssignment(doctor) {
  selectedDoctor.value = doctor
  assignmentError.value = ''
  assignmentSuccess.value = ''
  loadingAssignments.value = true

  try {
    const [allPatients, assignedPatients] = await Promise.all([
      getAdminUsers('PATIENT'),
      getDoctorAssignedPatients(doctor.id),
    ])

    patients.value = allPatients

    selectedPatientIds.value = assignedPatients.map((patient) => patient.id)
  } catch (err) {
    console.error('Could not load doctor assignments:', err)

    assignmentError.value = 'Patient assignments could not be loaded.'
  } finally {
    loadingAssignments.value = false
  }
}

function closePatientAssignment() {
  selectedDoctor.value = null
  patients.value = []
  selectedPatientIds.value = []

  assignmentError.value = ''
  assignmentSuccess.value = ''
}

function togglePatient(patientId) {
  if (selectedPatientIds.value.includes(patientId)) {
    selectedPatientIds.value = selectedPatientIds.value.filter((id) => id !== patientId)

    return
  }

  selectedPatientIds.value.push(patientId)
}

async function saveAssignments() {
  if (!selectedDoctor.value) {
    return
  }

  savingAssignments.value = true
  assignmentError.value = ''
  assignmentSuccess.value = ''

  try {
    await updateDoctorPatientAssignments(selectedDoctor.value.id, selectedPatientIds.value)

    assignmentSuccess.value = 'Patient assignments updated successfully.'

    await loadDoctors()
  } catch (err) {
    console.error('Could not update assignments:', err)

    assignmentError.value = err.message || 'Patient assignments could not be updated.'
  } finally {
    savingAssignments.value = false
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

const doctors = ref([])
const patients = ref([])

const selectedDoctor = ref(null)
const selectedPatientIds = ref([])

const loading = ref(true)
const error = ref('')

const loadingAssignments = ref(false)
const savingAssignments = ref(false)

const assignmentError = ref('')
const assignmentSuccess = ref('')

const search = ref('')

const filteredDoctors = computed(() => {
  const value = search.value.trim().toLowerCase()

  if (!value) {
    return doctors.value
  }

  return doctors.value.filter(
    (doctor) =>
      doctor.username?.toLowerCase().includes(value) || doctor.email?.toLowerCase().includes(value),
  )
})

onMounted(loadDoctors)
</script>

<template>
  <section>
    <h1 class="page-title">Doctors</h1>

    <p class="page-subtitle">View doctors and manage their assigned patients.</p>

    <div class="patients-toolbar">
      <h2 class="section-title">Doctor List</h2>

      <input v-model="search" class="search-input" type="text" placeholder="Search doctors..." />
    </div>

    <p v-if="loading" class="loading-message">Loading doctors...</p>

    <p v-else-if="error" class="error-message">
      {{ error }}
    </p>

    <div v-else class="table-card">
      <table>
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Status</th>
            <th>Last Login</th>
            <th>Patients</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="doctor in filteredDoctors" :key="doctor.id">
            <td class="patient-name">
              {{ doctor.username }}
            </td>

            <td>
              {{ doctor.email }}
            </td>

            <td>
              <span class="status-badge" :class="doctor.active ? 'taken' : 'missed'">
                <span class="status-dot"></span>

                {{ doctor.active ? 'Active' : 'Inactive' }}
              </span>
            </td>

            <td>
              {{ formatLastLogin(doctor.lastLoginAt) }}
            </td>

            <td>
              <button type="button" class="primary-button" @click="openPatientAssignment(doctor)">
                Manage Patients
              </button>
            </td>
          </tr>

          <tr v-if="filteredDoctors.length === 0">
            <td colspan="6">No doctors found.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="selectedDoctor" class="medication-form-card">
      <h2>
        Patients for
        {{ selectedDoctor.username }}
      </h2>

      <div class="form-content">
        <p v-if="loadingAssignments" class="loading-message">Loading patients...</p>

        <p v-else-if="assignmentError" class="error-message">
          {{ assignmentError }}
        </p>

        <template v-else>
          <div v-for="patient in patients" :key="patient.id" class="profile-row">
            <div>
              <strong>
                {{ patient.username }}
              </strong>

              <div>
                {{ patient.email }}
              </div>
            </div>

            <input
              type="checkbox"
              :checked="selectedPatientIds.includes(patient.id)"
              @change="togglePatient(patient.id)"
            />
          </div>

          <p v-if="patients.length === 0" class="empty-message">No patients are available.</p>

          <p v-if="assignmentSuccess" class="success-message">
            {{ assignmentSuccess }}
          </p>
        </template>
      </div>

      <div class="form-footer">
        <div class="form-actions">
          <button
            type="button"
            class="primary-button"
            :disabled="savingAssignments || loadingAssignments"
            @click="saveAssignments"
          >
            {{ savingAssignments ? 'Saving...' : 'Save Assignments' }}
          </button>

          <button type="button" class="secondary-button" @click="closePatientAssignment">
            Close
          </button>
        </div>
      </div>
    </div>
  </section>
</template>
