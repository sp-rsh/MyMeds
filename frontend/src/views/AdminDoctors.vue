<template>
  <div class="admin-page">

    <header class="page-header">
      <div>
        <p class="eyebrow">
          ADMINISTRATION
        </p>

        <h1 class="page-title">
          Doctors
        </h1>

        <p class="page-subtitle">
          View doctor accounts and manage patient assignments.
        </p>
      </div>
    </header>



    <div
      v-if="errorMessage"
      class="error-box"
    >
      <CircleAlert :size="20" />

      <div>
        <strong>
          Unable to load doctors
        </strong>

        <p>
          {{ errorMessage }}
        </p>
      </div>

      <button
        type="button"
        class="retry-button"
        @click="loadDoctors"
      >
        Retry
      </button>
    </div>


    <section class="panel">

      <div class="toolbar">

        <div class="search-box">
          <Search :size="19" />

          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search doctors..."
          />
        </div>

      </div>


      <!-- Loading -->

      <div
        v-if="loading"
        class="loading-state"
      >
        <LoaderCircle
          :size="30"
          class="loading-icon"
        />

        <strong>
          Loading doctors...
        </strong>
      </div>


      <!-- Table -->

      <div
        v-else
        class="table-wrapper"
      >

        <table>

          <thead>
            <tr>
              <th>
                Doctor
              </th>

              <th>
                Assigned Patients
              </th>

              <th class="actions-column">
                Actions
              </th>
            </tr>
          </thead>


          <tbody>

            <tr
              v-for="doctor in filteredDoctors"
              :key="doctor.id"
            >

              <td>
                <div class="user-cell">

                  <div class="avatar">
                    {{ getInitials(doctor.name) }}
                  </div>

                  <div>
                    <strong>
                      {{ doctor.name }}
                    </strong>

                    <span>
                      {{ doctor.email || 'No email available' }}
                    </span>
                  </div>

                </div>
              </td>


              <td>
                <span class="patient-count">
                  {{ doctor.patientCount }}
                </span>
              </td>


              <td class="actions-column">

                <button
                  class="assignment-button"
                  type="button"
                  :disabled="saving"
                  @click="openAssignments(doctor)"
                >
                  <Users :size="17" />

                  Manage Assignments
                </button>

              </td>

            </tr>


            <tr
              v-if="
                !errorMessage &&
                filteredDoctors.length === 0
              "
            >

              <td colspan="3">

                <div class="empty-state">

                  <Stethoscope :size="30" />

                  <strong>
                    No doctors available
                  </strong>

                  <span v-if="doctors.length === 0">
                    No doctor accounts were returned by the backend.
                  </span>

                  <span v-else>
                    No doctors match the current search.
                  </span>

                </div>

              </td>

            </tr>

          </tbody>

        </table>

      </div>

    </section>


    <!-- Information -->

    <section class="info-box">

      <div class="info-icon">
        <ShieldCheck :size="21" />
      </div>

      <div>

        <h3>
          Doctor assignments
        </h3>

        <p>
          Administrators manage doctor-patient assignments.
          Doctor account details and account status are managed
          from the Users page. Medication prescribing, dosage
          changes, and other clinical decisions remain under
          doctor control.
        </p>

      </div>

    </section>


    <!-- Assignment modal -->

    <div
      v-if="showAssignmentModal"
      class="modal-overlay"
      @click.self="closeAssignments"
    >

      <div class="modal">

        <div class="modal-header">

          <div>

            <p class="modal-eyebrow">
              PATIENT ASSIGNMENTS
            </p>

            <h2>
              {{ selectedDoctor?.name }}
            </h2>

          </div>


          <button
            class="close-button"
            type="button"
            :disabled="saving"
            @click="closeAssignments"
          >
            <X :size="20" />
          </button>

        </div>


        <div
          v-if="modalError"
          class="modal-error"
        >
          {{ modalError }}
        </div>


        <div
          v-if="assignmentLoading"
          class="modal-loading"
        >
          <LoaderCircle
            :size="25"
            class="loading-icon"
          />

          <span>
            Loading patients...
          </span>
        </div>


        <template v-else>

          <div
            v-if="patients.length === 0"
            class="modal-empty-state"
          >

            <Users :size="28" />

            <strong>
              No patients available
            </strong>

            <span>
              No patient accounts were returned by the backend.
            </span>

          </div>


          <div
            v-else
            class="patient-list"
          >

            <label
              v-for="patient in patients"
              :key="patient.id"
              class="patient-option"
            >

              <input
                v-model="selectedPatientIds"
                type="checkbox"
                :value="patient.id"
              />

              <div>

                <strong>
                  {{ patient.name }}
                </strong>

                <span>
                  {{ patient.email || 'No email available' }}
                </span>

              </div>

            </label>

          </div>

        </template>


        <div class="modal-actions">

          <button
            class="secondary-button"
            type="button"
            :disabled="saving"
            @click="closeAssignments"
          >
            Cancel
          </button>


          <button
            class="primary-button"
            type="button"
            :disabled="
              saving ||
              assignmentLoading ||
              patients.length === 0
            "
            @click="saveAssignments"
          >
            {{
              saving
                ? 'Saving...'
                : 'Save Assignments'
            }}
          </button>

        </div>

      </div>

    </div>

  </div>
</template>


<script setup>
import {
  computed,
  ref,
} from 'vue'

import {
  CircleAlert,
  LoaderCircle,
  Search,
  ShieldCheck,
  Stethoscope,
  Users,
  X,
} from '@lucide/vue'

import {
  getAdminUsers,
  getDoctorAssignedPatients,
  updateDoctorPatientAssignments,
} from '../services/api'



const doctors = ref([])
const patients = ref([])

const loading = ref(false)
const assignmentLoading = ref(false)
const saving = ref(false)

const errorMessage = ref('')
const modalError = ref('')

const searchQuery = ref('')

const showAssignmentModal = ref(false)

const selectedDoctor = ref(null)
const selectedPatientIds = ref([])



async function loadDoctors() {
  loading.value = true
  errorMessage.value = ''

  try {
    const result =
      await getAdminUsers('DOCTOR')


    const backendDoctors =
      getArrayResult(result)


    const normalizedDoctors =
      backendDoctors.map(normalizeUser)


    const doctorsWithCounts =
      await Promise.all(
        normalizedDoctors.map(
          async (doctor) => {

            try {
              const assigned =
                await getDoctorAssignedPatients(
                  doctor.id
                )


              const assignedPatients =
                getArrayResult(assigned)


              return {
                ...doctor,

                patientCount:
                  assignedPatients.length,

                patientIds:
                  assignedPatients
                    .map(getUserId)
                    .filter(
                      id =>
                        id !== null &&
                        id !== undefined
                    ),
              }

            } catch (error) {
              console.error(
                `Failed to load patients for doctor ${doctor.id}:`,
                error
              )


              return {
                ...doctor,
                patientCount: 0,
                patientIds: [],
              }
            }
          }
        )
      )


    doctors.value =
      doctorsWithCounts

  } catch (error) {
    console.error(
      'Failed to load doctors:',
      error
    )

    doctors.value = []

    errorMessage.value =
      getErrorMessage(error)

  } finally {
    loading.value = false
  }
}



async function openAssignments(doctor) {
  selectedDoctor.value = doctor

  selectedPatientIds.value = []

  patients.value = []

  modalError.value = ''

  showAssignmentModal.value = true

  assignmentLoading.value = true


  try {

    const patientResult =
      await getAdminUsers('PATIENT')


    patients.value =
      getArrayResult(patientResult)
        .map(normalizeUser)


    const assignedResult =
      await getDoctorAssignedPatients(
        doctor.id
      )


    const assignedPatients =
      getArrayResult(assignedResult)


    selectedPatientIds.value =
      assignedPatients
        .map(getUserId)
        .filter(
          id =>
            id !== null &&
            id !== undefined
        )

  } catch (error) {
    console.error(
      'Failed to load doctor assignments:',
      error
    )

    modalError.value =
      getErrorMessage(error)

  } finally {
    assignmentLoading.value = false
  }
}



function closeAssignments() {
  if (saving.value) {
    return
  }

  showAssignmentModal.value = false

  selectedDoctor.value = null

  selectedPatientIds.value = []

  patients.value = []

  modalError.value = ''
}



async function saveAssignments() {
  if (!selectedDoctor.value) {
    return
  }


  saving.value = true
  modalError.value = ''


  try {

    await updateDoctorPatientAssignments(
      selectedDoctor.value.id,
      selectedPatientIds.value
    )


    showAssignmentModal.value = false

    selectedDoctor.value = null
    selectedPatientIds.value = []
    patients.value = []


    await loadDoctors()

  } catch (error) {
    console.error(
      'Failed to save assignments:',
      error
    )

    modalError.value =
      getErrorMessage(error)

  } finally {
    saving.value = false
  }
}


function normalizeUser(user) {
  return {
    id:
      getUserId(user),

    name:
      user.name ??
      user.fullName ??
      user.username ??
      'Unnamed user',

    email:
      user.email ??
      '',
  }
}


function getUserId(user) {
  return (
    user?.id ??
    user?.userId ??
    user?.username ??
    null
  )
}


function getArrayResult(result) {
  if (Array.isArray(result)) {
    return result
  }


  if (Array.isArray(result?.content)) {
    return result.content
  }


  if (Array.isArray(result?.users)) {
    return result.users
  }


  if (Array.isArray(result?.patients)) {
    return result.patients
  }


  return []
}


const filteredDoctors = computed(() => {

  const query =
    searchQuery.value
      .trim()
      .toLowerCase()


  return doctors.value.filter(
    (doctor) => {

      return (
        !query ||
        doctor.name
          .toLowerCase()
          .includes(query) ||
        doctor.email
          .toLowerCase()
          .includes(query)
      )
    }
  )
})


function getInitials(name) {
  return String(name || '')
    .replace(/^Dr\.\s*/i, '')
    .split(' ')
    .filter(Boolean)
    .map(part => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()
}


function getErrorMessage(error) {
  if (error?.status === 401) {
    return 'Authentication is required.'
  }


  if (error?.status === 403) {
    return 'You do not have permission to perform this action.'
  }


  if (error?.status >= 500) {
    return 'The server is currently unavailable.'
  }


  return (
    error?.message ||
    'Something went wrong.'
  )
}


loadDoctors()
</script>


<style scoped>
.admin-page {
  width: 100%;
}


.page-header {
  margin-bottom: 30px;
}


.eyebrow {
  margin: 0 0 8px;

  color: #2563eb;

  font-size: 12px;
  font-weight: 700;

  letter-spacing: 1.2px;
}


.page-title {
  margin: 0;

  color: #172033;

  font-size: 38px;
  font-weight: 700;

  letter-spacing: -1px;
}


.page-subtitle {
  margin: 9px 0 0;

  color: #64748b;

  font-size: 17px;
}


.error-box {
  display: flex;
  align-items: center;

  gap: 12px;

  margin-bottom: 20px;

  padding: 15px 17px;

  border: 1px solid #fecaca;
  border-radius: 11px;

  background: #fef2f2;

  color: #b91c1c;
}


.error-box > svg {
  flex-shrink: 0;
}


.error-box div {
  flex: 1;
}


.error-box strong {
  display: block;

  font-size: 13px;
}


.error-box p {
  margin: 3px 0 0;

  font-size: 12px;
}


.retry-button {
  padding: 7px 12px;

  border: 1px solid #fecaca;
  border-radius: 7px;

  background: #ffffff;

  color: #b91c1c;

  font-size: 12px;
  font-weight: 600;

  cursor: pointer;
}


.panel {
  overflow: hidden;

  background: #ffffff;

  border: 1px solid #e2e8f0;
  border-radius: 14px;
}



.toolbar {
  display: flex;

  padding: 18px 20px;

  border-bottom: 1px solid #e2e8f0;
}


.search-box {
  position: relative;

  flex: 1;

  max-width: 420px;
}


.search-box svg {
  position: absolute;

  top: 50%;
  left: 13px;

  transform: translateY(-50%);

  color: #94a3b8;
}


.search-box input {
  width: 100%;
  height: 42px;

  padding: 0 14px 0 41px;

  border: 1px solid #e2e8f0;
  border-radius: 8px;

  outline: none;

  color: #172033;

  background: #ffffff;
}


.search-box input:focus {
  border-color: #93c5fd;

  box-shadow: 0 0 0 3px #eff6ff;
}



.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;

  gap: 10px;

  padding: 60px 20px;

  color: #64748b;
}


.loading-state strong {
  font-size: 13px;
}


.loading-icon {
  animation: spin 0.9s linear infinite;
}


@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}



.table-wrapper {
  overflow-x: auto;
}


table {
  width: 100%;

  min-width: 650px;

  border-collapse: collapse;
}


th {
  padding: 14px 20px;

  background: #f8fafc;

  border-bottom: 1px solid #e2e8f0;

  color: #64748b;

  font-size: 11px;
  font-weight: 700;

  text-align: left;

  text-transform: uppercase;

  letter-spacing: 0.7px;
}


td {
  padding: 16px 20px;

  border-bottom: 1px solid #f1f5f9;

  vertical-align: middle;
}


tbody tr:hover {
  background: #fafcff;
}



.user-cell {
  display: flex;
  align-items: center;

  gap: 12px;
}


.avatar {
  width: 40px;
  height: 40px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 50%;

  background: #f5f3ff;

  color: #7c3aed;

  font-size: 12px;
  font-weight: 700;
}


.user-cell strong {
  display: block;

  color: #172033;

  font-size: 14px;
}


.user-cell span {
  display: block;

  margin-top: 3px;

  color: #94a3b8;

  font-size: 12px;
}


.patient-count {
  color: #64748b;

  font-size: 13px;
}


.actions-column {
  text-align: right !important;
}



.assignment-button {
  display: inline-flex;
  align-items: center;

  gap: 7px;

  padding: 8px 11px;

  border: 1px solid #e2e8f0;
  border-radius: 8px;

  background: #ffffff;

  color: #475569;

  font-size: 13px;
  font-weight: 600;

  cursor: pointer;
}


.assignment-button:hover:not(:disabled) {
  background: #f8fafc;

  color: #2563eb;
}


.assignment-button:disabled {
  opacity: 0.5;

  cursor: not-allowed;
}



.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;

  gap: 7px;

  padding: 60px 20px;

  color: #94a3b8;

  text-align: center;
}


.empty-state strong {
  color: #475569;

  font-size: 14px;
}


.empty-state span {
  max-width: 430px;

  font-size: 13px;

  line-height: 1.5;
}



.info-box {
  display: flex;
  align-items: flex-start;

  gap: 14px;

  margin-top: 24px;

  padding: 20px 22px;

  background: #eff6ff;

  border: 1px solid #dbeafe;
  border-radius: 14px;
}


.info-icon {
  width: 42px;
  height: 42px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  background: #ffffff;

  color: #2563eb;

  border-radius: 10px;
}


.info-box h3 {
  margin: 0;

  color: #172033;

  font-size: 14px;
}


.info-box p {
  margin: 5px 0 0;

  color: #64748b;

  font-size: 13px;

  line-height: 1.5;
}


.modal-overlay {
  position: fixed;

  inset: 0;

  display: flex;
  align-items: center;
  justify-content: center;

  padding: 20px;

  background: rgba(15, 23, 42, 0.42);

  z-index: 1000;
}


.modal {
  width: min(520px, 100%);

  padding: 26px;

  background: #ffffff;

  border-radius: 16px;
}


.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;

  margin-bottom: 24px;
}


.modal-eyebrow {
  margin: 0 0 5px;

  color: #2563eb;

  font-size: 10px;
  font-weight: 700;

  letter-spacing: 1px;
}


.modal h2 {
  margin: 0;

  color: #172033;
}


.close-button {
  width: 36px;
  height: 36px;

  display: flex;
  align-items: center;
  justify-content: center;

  border: none;
  border-radius: 8px;

  background: #f8fafc;

  color: #64748b;

  cursor: pointer;
}


.close-button:disabled {
  opacity: 0.5;

  cursor: not-allowed;
}


.modal-error {
  margin-bottom: 18px;

  padding: 10px 12px;

  border: 1px solid #fecaca;
  border-radius: 8px;

  background: #fef2f2;

  color: #b91c1c;

  font-size: 13px;
}


.modal-loading {
  display: flex;
  align-items: center;
  justify-content: center;

  gap: 9px;

  padding: 36px 20px;

  color: #64748b;

  font-size: 13px;
}


.modal-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;

  gap: 7px;

  padding: 36px 20px;

  color: #94a3b8;

  text-align: center;
}


.modal-empty-state strong {
  color: #475569;

  font-size: 14px;
}


.modal-empty-state span {
  max-width: 340px;

  font-size: 13px;

  line-height: 1.5;
}



.patient-list {
  display: grid;

  gap: 10px;

  max-height: 330px;

  overflow-y: auto;
}


.patient-option {
  display: flex;
  align-items: center;

  gap: 12px;

  padding: 12px 14px;

  border: 1px solid #e2e8f0;
  border-radius: 9px;

  cursor: pointer;
}


.patient-option:hover {
  background: #f8fafc;
}


.patient-option input {
  width: 16px;
  height: 16px;
}


.patient-option strong {
  display: block;

  color: #172033;

  font-size: 13px;
}


.patient-option span {
  display: block;

  margin-top: 3px;

  color: #94a3b8;

  font-size: 12px;
}



.primary-button,
.secondary-button {
  padding: 10px 16px;

  border-radius: 8px;

  font-weight: 600;

  cursor: pointer;
}


.primary-button {
  border: none;

  background: #2563eb;

  color: #ffffff;
}


.primary-button:hover:not(:disabled) {
  background: #1d4ed8;
}


.primary-button:disabled {
  opacity: 0.5;

  cursor: not-allowed;
}


.secondary-button {
  border: 1px solid #e2e8f0;

  background: #ffffff;

  color: #475569;
}


.secondary-button:hover:not(:disabled) {
  background: #f8fafc;
}


.secondary-button:disabled {
  opacity: 0.5;

  cursor: not-allowed;
}


.modal-actions {
  display: flex;
  justify-content: flex-end;

  gap: 10px;

  margin-top: 25px;

  padding-top: 20px;

  border-top: 1px solid #f1f5f9;
}



@media (max-width: 700px) {

  .toolbar {
    display: block;
  }


  .search-box {
    max-width: none;
  }
}
</style>
