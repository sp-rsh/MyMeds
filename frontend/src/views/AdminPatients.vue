<template>
  <div class="admin-page">

    <header class="page-header">
      <div>
        <p class="eyebrow">
          ADMINISTRATION
        </p>

        <h1 class="page-title">
          Patients
        </h1>

        <p class="page-subtitle">
          View patient accounts and manage doctor assignments.
        </p>
      </div>
    </header>


    <section class="panel">

      <div class="toolbar">

        <div class="search-box">
          <Search :size="19" />

          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search patients..."
          />
        </div>

        <select
          v-model="doctorFilter"
          class="filter-select"
        >
          <option value="All">
            All doctors
          </option>

          <option value="Unassigned">
            Unassigned
          </option>

          <option
            v-for="doctor in doctors"
            :key="doctor.id"
            :value="doctor.id"
          >
            {{ doctor.name }}
          </option>
        </select>

      </div>


      <div class="table-wrapper">

        <table>

          <thead>
            <tr>
              <th>Patient</th>
              <th>Assigned Doctor</th>
              <th class="actions-column">
                Actions
              </th>
            </tr>
          </thead>


          <tbody>

            <tr
              v-for="patient in filteredPatients"
              :key="patient.id"
            >

              <td>
                <div class="user-cell">

                  <div class="avatar">
                    {{ getInitials(patient.name) }}
                  </div>

                  <div>
                    <strong>
                      {{ patient.name }}
                    </strong>

                    <span>
                      {{ patient.email }}
                    </span>
                  </div>

                </div>
              </td>


              <td>
                <span class="doctor-name">
                  {{ getDoctorName(patient.doctorId) }}
                </span>
              </td>


              <td class="actions-column">

                <button
                  class="assignment-button"
                  type="button"
                  @click="openAssignment(patient)"
                >
                  <Stethoscope :size="17" />

                  {{
                    patient.doctorId
                      ? 'Change Doctor'
                      : 'Assign Doctor'
                  }}
                </button>

              </td>

            </tr>


            <tr v-if="filteredPatients.length === 0">

              <td colspan="3">

                <div class="empty-state">

                  <Users :size="30" />

                  <strong>
                    No patients available
                  </strong>

                  <span>
                    Patient accounts will appear here once they are
                    loaded from the backend.
                  </span>

                </div>

              </td>

            </tr>

          </tbody>

        </table>

      </div>

    </section>


    <section class="info-box">

      <div class="info-icon">
        <ShieldCheck :size="21" />
      </div>

      <div>

        <h3>
          Patient assignments
        </h3>

        <p>
          Administrators manage doctor-patient assignments.
          Patient account details and status are managed from the
          Users page. Medication schedules and clinical decisions
          remain under doctor control.
        </p>

      </div>

    </section>


    <!-- Assignment modal -->

    <div
      v-if="showAssignmentModal"
      class="modal-overlay"
      @click.self="closeAssignment"
    >

      <div class="modal">

        <div class="modal-header">

          <div>
            <p class="modal-eyebrow">
              DOCTOR ASSIGNMENT
            </p>

            <h2>
              {{ selectedPatient?.name }}
            </h2>
          </div>


          <button
            class="close-button"
            type="button"
            @click="closeAssignment"
          >
            <X :size="20" />
          </button>

        </div>


        <div
          v-if="doctors.length === 0"
          class="modal-empty-state"
        >

          <Stethoscope :size="28" />

          <strong>
            No doctors available
          </strong>

          <span>
            Doctor accounts will appear here once they are loaded
            from the backend.
          </span>

        </div>


        <div
          v-else
          class="assignment-form"
        >

          <label>
            <span>
              Assigned Doctor
            </span>

            <select v-model="selectedDoctorId">

              <option value="">
                Unassigned
              </option>

              <option
                v-for="doctor in doctors"
                :key="doctor.id"
                :value="doctor.id"
              >
                {{ doctor.name }}
              </option>

            </select>
          </label>

        </div>


        <div class="modal-actions">

          <button
            class="secondary-button"
            type="button"
            @click="closeAssignment"
          >
            Cancel
          </button>


          <button
            class="primary-button"
            type="button"
            :disabled="doctors.length === 0"
            @click="saveAssignment"
          >
            Save Assignment
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
  Users,
  Stethoscope,
  Search,
  ShieldCheck,
  X,
} from '@lucide/vue'


const patients = ref([])

const doctors = ref([])

const searchQuery = ref('')

const doctorFilter = ref('All')

const showAssignmentModal = ref(false)

const selectedPatient = ref(null)

const selectedDoctorId = ref('')


const filteredPatients = computed(() => {

  const query =
    searchQuery.value
      .trim()
      .toLowerCase()


  return patients.value.filter((patient) => {

    const matchesSearch =
      !query ||
      patient.name
        .toLowerCase()
        .includes(query) ||
      patient.email
        .toLowerCase()
        .includes(query)


    let matchesDoctor = true


    if (doctorFilter.value === 'Unassigned') {

      matchesDoctor = !patient.doctorId

    } else if (doctorFilter.value !== 'All') {

      matchesDoctor =
        String(patient.doctorId) ===
        String(doctorFilter.value)
    }


    return (
      matchesSearch &&
      matchesDoctor
    )
  })
})


function openAssignment(patient) {

  selectedPatient.value = patient

  selectedDoctorId.value =
    patient.doctorId ?? ''

  showAssignmentModal.value = true
}


function closeAssignment() {

  showAssignmentModal.value = false

  selectedPatient.value = null

  selectedDoctorId.value = ''
}


function saveAssignment() {

  if (!selectedPatient.value) {
    return
  }


  selectedPatient.value.doctorId =
    selectedDoctorId.value || null


  closeAssignment()
}


function getDoctorName(doctorId) {

  if (!doctorId) {
    return 'Unassigned'
  }


  const doctor =
    doctors.value.find(
      item =>
        String(item.id) ===
        String(doctorId)
    )


  return doctor?.name || 'Unassigned'
}


function getInitials(name) {

  return String(name || '')
    .split(' ')
    .filter(Boolean)
    .map(part => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()
}
</script>


<style scoped>
.admin-page {
  width: 100%;
}


/* Header */

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


/* Panel */

.panel {
  overflow: hidden;

  background: #ffffff;

  border: 1px solid #e2e8f0;
  border-radius: 14px;
}


/* Toolbar */

.toolbar {
  display: flex;
  align-items: center;

  gap: 12px;

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


.filter-select {
  height: 42px;

  padding: 0 32px 0 12px;

  border: 1px solid #e2e8f0;
  border-radius: 8px;

  background: #ffffff;

  color: #475569;

  outline: none;
}


/* Table */

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


/* Patient */

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

  background: #eff6ff;

  color: #2563eb;

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


.doctor-name {
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


.assignment-button:hover {
  background: #f8fafc;

  color: #2563eb;
}


/* Empty */

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


/* Information */

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


/* Modal */

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


/* Assignment */

.assignment-form {
  display: grid;

  gap: 16px;
}


.assignment-form label {
  display: grid;

  gap: 7px;
}


.assignment-form label span {
  color: #475569;

  font-size: 13px;
  font-weight: 600;
}


.assignment-form select {
  height: 42px;

  padding: 0 12px;

  border: 1px solid #e2e8f0;
  border-radius: 8px;

  background: #ffffff;

  color: #172033;

  outline: none;
}


.assignment-form select:focus {
  border-color: #93c5fd;

  box-shadow: 0 0 0 3px #eff6ff;
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


/* Buttons */

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


.secondary-button:hover {
  background: #f8fafc;
}


.modal-actions {
  display: flex;
  justify-content: flex-end;

  gap: 10px;

  margin-top: 25px;

  padding-top: 20px;

  border-top: 1px solid #f1f5f9;
}


/* Responsive */

@media (max-width: 700px) {

  .toolbar {
    flex-direction: column;

    align-items: stretch;
  }


  .search-box {
    max-width: none;
  }
}
</style>
