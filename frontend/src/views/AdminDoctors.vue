<template>
  <div class="admin-page">

    <header class="page-header">
      <div>
        <p class="eyebrow">ADMINISTRATION</p>
        <h1 class="page-title">Doctors</h1>
        <p class="page-subtitle">
          Manage doctor accounts and patient assignments.
        </p>
      </div>

      <button class="primary-button" @click="openAddDoctor">
        <UserPlus :size="18" />
        Add Doctor
      </button>
    </header>


    <!-- Summary -->
    <section class="summary-grid">
      <div class="summary-card">
        <div class="summary-icon purple">
          <Stethoscope :size="21" />
        </div>
        <div>
          <span>Total Doctors</span>
          <strong>{{ doctors.length }}</strong>
        </div>
      </div>

      <div class="summary-card">
        <div class="summary-icon green">
          <UserCheck :size="21" />
        </div>
        <div>
          <span>Active Doctors</span>
          <strong>{{ activeDoctors }}</strong>
        </div>
      </div>

      <div class="summary-card">
        <div class="summary-icon blue">
          <Users :size="21" />
        </div>
        <div>
          <span>Assigned Patients</span>
          <strong>{{ assignedPatients }}</strong>
        </div>
      </div>
    </section>


    <!-- Search -->
    <section class="panel">

      <div class="toolbar">
        <div class="search-box">
          <Search :size="19" />

          <input
            v-model="searchQuery"
            placeholder="Search doctors..."
          />
        </div>

        <select v-model="statusFilter">
          <option value="All">All statuses</option>
          <option value="Active">Active</option>
          <option value="Inactive">Inactive</option>
        </select>
      </div>


      <!-- Table -->
      <div class="table-wrapper">
        <table>

          <thead>
            <tr>
              <th>Doctor</th>
              <th>Specialization</th>
              <th>Patients</th>
              <th>Status</th>
              <th>Last Login</th>
              <th>Actions</th>
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
                    <strong>{{ doctor.name }}</strong>
                    <span>{{ doctor.email }}</span>
                  </div>
                </div>
              </td>

              <td>
                <span class="specialization">
                  {{ doctor.specialization }}
                </span>
              </td>

              <td>
                <span class="patient-count">
                  {{ doctor.patients }}
                </span>
              </td>

              <td>
                <span
                  class="status"
                  :class="doctor.status === 'Active'
                    ? 'active'
                    : 'inactive'"
                >
                  <span></span>
                  {{ doctor.status }}
                </span>
              </td>

              <td class="muted">
                {{ doctor.lastLogin }}
              </td>

              <td>
                <div class="actions">

                  <button
                    class="icon-button"
                    title="Edit doctor"
                    @click="editDoctor(doctor)"
                  >
                    <Pencil :size="17" />
                  </button>

                  <button
                    class="icon-button"
                    :class="doctor.status === 'Active'
                      ? 'danger'
                      : 'success'"
                    :title="doctor.status === 'Active'
                      ? 'Deactivate'
                      : 'Activate'"
                    @click="toggleStatus(doctor)"
                  >
                    <UserX
                      v-if="doctor.status === 'Active'"
                      :size="17"
                    />

                    <UserCheck
                      v-else
                      :size="17"
                    />
                  </button>

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
        <h3>Doctor account administration</h3>
        <p>
          Administrators manage doctor accounts and assignments.
          Medication prescribing, dosage changes, and clinical
          decisions remain under the doctor's responsibility.
        </p>
      </div>
    </section>


    <!-- Modal -->
    <div
      v-if="showModal"
      class="modal-overlay"
      @click.self="closeModal"
    >
      <div class="modal">

        <div class="modal-header">
          <div>
            <p class="modal-eyebrow">
              {{ editingDoctor ? 'EDIT DOCTOR' : 'NEW DOCTOR' }}
            </p>

            <h2>
              {{ editingDoctor ? 'Edit Doctor' : 'Add Doctor' }}
            </h2>
          </div>

          <button
            class="close-button"
            @click="closeModal"
          >
            <X :size="20" />
          </button>
        </div>


        <div class="form">

          <label>
            Full Name
            <input
              v-model="form.name"
              placeholder="Dr. John Smith"
            />
          </label>

          <label>
            Email
            <input
              v-model="form.email"
              type="email"
              placeholder="doctor@mymeds.example.com"
            />
          </label>

          <label>
            Specialization
            <select v-model="form.specialization">
              <option>General Medicine</option>
              <option>Cardiology</option>
              <option>Internal Medicine</option>
              <option>Family Medicine</option>
              <option>Neurology</option>
            </select>
          </label>

        </div>


        <div class="modal-actions">
          <button
            class="secondary-button"
            @click="closeModal"
          >
            Cancel
          </button>

          <button
            class="primary-button"
            @click="saveDoctor"
          >
            {{ editingDoctor ? 'Save Changes' : 'Create Doctor' }}
          </button>
        </div>

      </div>
    </div>

  </div>
</template>


<script setup>
import { computed, reactive, ref } from 'vue'

import {
  Stethoscope,
  UserPlus,
  UserCheck,
  UserX,
  Users,
  Search,
  Pencil,
  ShieldCheck,
  X,
} from '@lucide/vue'


const doctors = ref([
  {
    id: 1,
    name: 'Dr. Sharma',
    email: 'sharma@mymeds.example.com',
    specialization: 'General Medicine',
    patients: 8,
    status: 'Active',
    lastLogin: 'Today, 10:05',
  },
  {
    id: 2,
    name: 'Dr. Mehta',
    email: 'mehta@mymeds.example.com',
    specialization: 'Internal Medicine',
    patients: 6,
    status: 'Active',
    lastLogin: 'Today, 09:11',
  },
  {
    id: 3,
    name: 'Dr. Kapoor',
    email: 'kapoor@mymeds.example.com',
    specialization: 'Cardiology',
    patients: 4,
    status: 'Inactive',
    lastLogin: 'Oct 12, 2026',
  },
])


const searchQuery = ref('')
const statusFilter = ref('All')

const showModal = ref(false)
const editingDoctor = ref(null)

const form = reactive({
  name: '',
  email: '',
  specialization: 'General Medicine',
})


const filteredDoctors = computed(() => {
  const query = searchQuery.value
    .trim()
    .toLowerCase()

  return doctors.value.filter((doctor) => {

    const matchesSearch =
      !query ||
      doctor.name.toLowerCase().includes(query) ||
      doctor.email.toLowerCase().includes(query) ||
      doctor.specialization.toLowerCase().includes(query)

    const matchesStatus =
      statusFilter.value === 'All' ||
      doctor.status === statusFilter.value

    return matchesSearch && matchesStatus
  })
})


const activeDoctors = computed(() =>
  doctors.value.filter(
    doctor => doctor.status === 'Active'
  ).length
)


const assignedPatients = computed(() =>
  doctors.value.reduce(
    (total, doctor) => total + doctor.patients,
    0
  )
)


function getInitials(name) {
  return name
    .replace('Dr. ', '')
    .split(' ')
    .map(part => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()
}


function openAddDoctor() {
  editingDoctor.value = null

  form.name = ''
  form.email = ''
  form.specialization = 'General Medicine'

  showModal.value = true
}


function editDoctor(doctor) {
  editingDoctor.value = doctor

  form.name = doctor.name
  form.email = doctor.email
  form.specialization = doctor.specialization

  showModal.value = true
}


function closeModal() {
  showModal.value = false
  editingDoctor.value = null
}


function saveDoctor() {
  if (!form.name.trim() || !form.email.trim()) {
    return
  }

  if (editingDoctor.value) {

    editingDoctor.value.name = form.name
    editingDoctor.value.email = form.email
    editingDoctor.value.specialization =
      form.specialization

  } else {

    doctors.value.push({
      id: Date.now(),
      name: form.name,
      email: form.email,
      specialization: form.specialization,
      patients: 0,
      status: 'Active',
      lastLogin: 'Never',
    })

  }

  closeModal()
}


function toggleStatus(doctor) {
  doctor.status =
    doctor.status === 'Active'
      ? 'Inactive'
      : 'Active'
}
</script>


<style scoped>
.admin-page {
  width: 100%;
}

.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 30px;
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

.primary-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 11px 17px;
  border: none;
  border-radius: 9px;
  background: #2563eb;
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.primary-button:hover {
  background: #1d4ed8;
}

.secondary-button {
  padding: 11px 17px;
  border: 1px solid #e2e8f0;
  border-radius: 9px;
  background: #ffffff;
  color: #475569;
  font-weight: 600;
  cursor: pointer;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
  margin-bottom: 24px;
}

.summary-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 13px;
}

.summary-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}

.summary-icon.blue {
  color: #2563eb;
  background: #eff6ff;
}

.summary-icon.purple {
  color: #7c3aed;
  background: #f5f3ff;
}

.summary-icon.green {
  color: #16a34a;
  background: #f0fdf4;
}

.summary-card span {
  display: block;
  color: #94a3b8;
  font-size: 12px;
}

.summary-card strong {
  display: block;
  margin-top: 3px;
  color: #172033;
  font-size: 24px;
}

.panel {
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
}

.toolbar {
  display: flex;
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
}

.toolbar select {
  height: 42px;
  padding: 0 32px 0 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #ffffff;
  color: #475569;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  min-width: 850px;
  border-collapse: collapse;
}

th {
  padding: 14px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  color: #64748b;
  font-size: 11px;
  text-align: left;
  text-transform: uppercase;
  letter-spacing: .7px;
}

td {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
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

.specialization,
.patient-count,
.muted {
  color: #64748b;
  font-size: 13px;
}

.status {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  font-size: 12px;
  font-weight: 600;
}

.status > span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}

.status.active {
  color: #15803d;
}

.status.active > span {
  background: #22c55e;
}

.status.inactive {
  color: #94a3b8;
}

.status.inactive > span {
  background: #94a3b8;
}

.actions {
  display: flex;
  gap: 6px;
}

.icon-button {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e2e8f0;
  border-radius: 7px;
  background: #ffffff;
  color: #64748b;
  cursor: pointer;
}

.icon-button:hover {
  color: #2563eb;
  background: #f8fafc;
}

.icon-button.danger:hover {
  color: #dc2626;
}

.icon-button.success:hover {
  color: #16a34a;
}

.info-box {
  display: flex;
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
  background: rgba(15, 23, 42, .42);
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
  border: none;
  border-radius: 8px;
  background: #f8fafc;
  cursor: pointer;
}

.form {
  display: grid;
  gap: 17px;
}

.form label {
  display: grid;
  gap: 7px;
  color: #475569;
  font-size: 13px;
  font-weight: 600;
}

.form input,
.form select {
  height: 42px;
  padding: 0 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  outline: none;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

@media (max-width: 800px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>