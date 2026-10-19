<template>
  <div class="users-page">

    <!-- Header -->
    <header class="page-header">
      <div>
        <p class="eyebrow">ADMINISTRATION</p>

        <h1 class="page-title">
          Users
        </h1>

        <p class="page-subtitle">
          Manage MyMeds user accounts, roles, and account status.
        </p>
      </div>

      <button class="primary-button" type="button" @click="openAddUser">
        <UserPlus :size="18" />
        <span>Add User</span>
      </button>
    </header>


    <!-- Summary -->
    <section class="summary-grid">

      <div class="summary-card">
        <div class="summary-icon blue">
          <Users :size="21" />
        </div>

        <div>
          <span>Total Users</span>
          <strong>{{ users.length }}</strong>
        </div>
      </div>

      <div class="summary-card">
        <div class="summary-icon purple">
          <Stethoscope :size="21" />
        </div>

        <div>
          <span>Doctors</span>
          <strong>{{ doctorCount }}</strong>
        </div>
      </div>

      <div class="summary-card">
        <div class="summary-icon green">
          <UserRound :size="21" />
        </div>

        <div>
          <span>Patients</span>
          <strong>{{ patientCount }}</strong>
        </div>
      </div>

      <div class="summary-card">
        <div class="summary-icon orange">
          <UserCheck :size="21" />
        </div>

        <div>
          <span>Active Accounts</span>
          <strong>{{ activeCount }}</strong>
        </div>
      </div>

    </section>


    <!-- Users Panel -->
    <section class="users-panel">

      <!-- Toolbar -->
      <div class="toolbar">

        <div class="search-box">
          <Search :size="19" />

          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search users..."
          />
        </div>

        <select v-model="roleFilter" class="filter-select">
          <option value="All">All roles</option>
          <option value="Admin">Admin</option>
          <option value="Doctor">Doctor</option>
          <option value="Patient">Patient</option>
        </select>

        <select v-model="statusFilter" class="filter-select">
          <option value="All">All statuses</option>
          <option value="Active">Active</option>
          <option value="Inactive">Inactive</option>
        </select>

      </div>


      <!-- Table -->
      <div class="table-wrapper">

        <table class="users-table">

          <thead>
            <tr>
              <th>User</th>
              <th>Role</th>
              <th>Status</th>
              <th>Assigned Doctor</th>
              <th>Last Login</th>
              <th class="actions-column">Actions</th>
            </tr>
          </thead>

          <tbody>

            <tr
              v-for="user in filteredUsers"
              :key="user.id"
            >

              <!-- User -->
              <td>
                <div class="user-cell">

                  <div class="avatar">
                    {{ getInitials(user.name) }}
                  </div>

                  <div>
                    <strong>{{ user.name }}</strong>
                    <span>{{ user.email }}</span>
                  </div>

                </div>
              </td>


              <!-- Role -->
              <td>
                <span
                  class="role-badge"
                  :class="getRoleClass(user.role)"
                >
                  {{ user.role }}
                </span>
              </td>


              <!-- Status -->
              <td>
                <span
                  class="status-badge"
                  :class="user.status === 'Active'
                    ? 'status-active'
                    : 'status-inactive'"
                >
                  <span></span>
                  {{ user.status }}
                </span>
              </td>


              <!-- Doctor -->
              <td>
                <span class="doctor-name">
                  {{ user.doctor || '—' }}
                </span>
              </td>


              <!-- Last Login -->
              <td>
                <span class="last-login">
                  {{ user.lastLogin }}
                </span>
              </td>


              <!-- Actions -->
              <td>
                <div class="action-buttons">

                  <button
                    class="icon-button"
                    type="button"
                    title="Edit user"
                    @click="editUser(user)"
                  >
                    <Pencil :size="17" />
                  </button>

                  <button
                    class="icon-button"
                    type="button"
                    title="Change role"
                    @click="changeRole(user)"
                  >
                    <ShieldCheck :size="17" />
                  </button>

                  <button
                    class="icon-button"
                    :class="{
                      'activate-button': user.status === 'Inactive',
                      'deactivate-button': user.status === 'Active'
                    }"
                    type="button"
                    :title="user.status === 'Active'
                      ? 'Deactivate user'
                      : 'Activate user'"
                    @click="toggleStatus(user)"
                  >
                    <UserCheck
                      v-if="user.status === 'Inactive'"
                      :size="17"
                    />

                    <UserX
                      v-else
                      :size="17"
                    />
                  </button>

                </div>
              </td>

            </tr>


            <!-- Empty state -->
            <tr v-if="filteredUsers.length === 0">
              <td colspan="6">
                <div class="empty-state">
                  <Search :size="28" />
                  <strong>No users found</strong>
                  <span>
                    Try changing your search or filters.
                  </span>
                </div>
              </td>
            </tr>

          </tbody>

        </table>

      </div>

    </section>


    <!-- Permission Info -->
    <section class="permission-info">

      <div class="permission-icon">
        <ShieldCheck :size="21" />
      </div>

      <div>
        <h3>Account administration only</h3>

        <p>
          Administrators can create accounts, manage roles,
          assign doctors, and activate or deactivate users.
          Medication prescribing and clinical changes remain
          under doctor control.
        </p>
      </div>

    </section>


    <!-- Add / Edit Modal -->
    <div
      v-if="showModal"
      class="modal-overlay"
      @click.self="closeModal"
    >

      <div class="modal">

        <div class="modal-header">
          <div>
            <p class="modal-eyebrow">
              {{ editingUser ? 'EDIT USER' : 'NEW USER' }}
            </p>

            <h2>
              {{ editingUser ? 'Edit User' : 'Add User' }}
            </h2>
          </div>

          <button
            class="close-button"
            type="button"
            @click="closeModal"
          >
            <X :size="20" />
          </button>
        </div>


        <div class="form-grid">

          <label>
            <span>Full Name</span>

            <input
              v-model="form.name"
              type="text"
              placeholder="Enter full name"
            />
          </label>


          <label>
            <span>Email Address</span>

            <input
              v-model="form.email"
              type="email"
              placeholder="Enter email address"
            />
          </label>


          <label>
            <span>Role</span>

            <select v-model="form.role">
              <option value="Patient">Patient</option>
              <option value="Doctor">Doctor</option>
              <option value="Admin">Admin</option>
            </select>
          </label>


          <label v-if="form.role === 'Patient'">
            <span>Assigned Doctor</span>

            <select v-model="form.doctor">
              <option value="">Select doctor</option>
              <option value="Dr. Sharma">Dr. Sharma</option>
              <option value="Dr. Mehta">Dr. Mehta</option>
              <option value="Dr. Kapoor">Dr. Kapoor</option>
            </select>
          </label>

        </div>


        <div class="modal-actions">

          <button
            class="secondary-button"
            type="button"
            @click="closeModal"
          >
            Cancel
          </button>

          <button
            class="primary-button"
            type="button"
            @click="saveUser"
          >
            {{ editingUser ? 'Save Changes' : 'Create User' }}
          </button>

        </div>

      </div>

    </div>

  </div>
</template>


<script setup>
import { computed, reactive, ref } from 'vue'

import {
  Users,
  UserPlus,
  UserRound,
  UserCheck,
  UserX,
  Stethoscope,
  Search,
  Pencil,
  ShieldCheck,
  X,
} from '@lucide/vue'


/* --------------------------------------------------
   Demo user data
-------------------------------------------------- */

const users = ref([
  {
    id: 1,
    name: 'Anvi Chahal',
    email: 'anvi.chahal@example.com',
    role: 'Patient',
    status: 'Active',
    doctor: 'Dr. Sharma',
    lastLogin: 'Today, 09:42',
  },
  {
    id: 2,
    name: 'Rahul Mehta',
    email: 'rahul.mehta@example.com',
    role: 'Patient',
    status: 'Active',
    doctor: 'Dr. Sharma',
    lastLogin: 'Today, 08:17',
  },
  {
    id: 3,
    name: 'Priya Kapoor',
    email: 'priya.kapoor@example.com',
    role: 'Patient',
    status: 'Active',
    doctor: 'Dr. Mehta',
    lastLogin: 'Yesterday, 18:31',
  },
  {
    id: 4,
    name: 'Dr. Sharma',
    email: 'sharma@mymeds.example.com',
    role: 'Doctor',
    status: 'Active',
    doctor: '',
    lastLogin: 'Today, 10:05',
  },
  {
    id: 5,
    name: 'Dr. Mehta',
    email: 'mehta@mymeds.example.com',
    role: 'Doctor',
    status: 'Active',
    doctor: '',
    lastLogin: 'Today, 09:11',
  },
  {
    id: 6,
    name: 'Dr. Kapoor',
    email: 'kapoor@mymeds.example.com',
    role: 'Doctor',
    status: 'Inactive',
    doctor: '',
    lastLogin: 'Oct 12, 2026',
  },
  {
    id: 7,
    name: 'System Administrator',
    email: 'admin@mymeds.example.com',
    role: 'Admin',
    status: 'Active',
    doctor: '',
    lastLogin: 'Today, 10:22',
  },
])


/* --------------------------------------------------
   Filters
-------------------------------------------------- */

const searchQuery = ref('')
const roleFilter = ref('All')
const statusFilter = ref('All')


const filteredUsers = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()

  return users.value.filter((user) => {

    const matchesSearch =
      !query ||
      user.name.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query)

    const matchesRole =
      roleFilter.value === 'All' ||
      user.role === roleFilter.value

    const matchesStatus =
      statusFilter.value === 'All' ||
      user.status === statusFilter.value

    return matchesSearch && matchesRole && matchesStatus
  })
})


/* --------------------------------------------------
   Summary
-------------------------------------------------- */

const doctorCount = computed(() => {
  return users.value.filter(
    user => user.role === 'Doctor'
  ).length
})


const patientCount = computed(() => {
  return users.value.filter(
    user => user.role === 'Patient'
  ).length
})


const activeCount = computed(() => {
  return users.value.filter(
    user => user.status === 'Active'
  ).length
})


/* --------------------------------------------------
   Modal
-------------------------------------------------- */

const showModal = ref(false)
const editingUser = ref(null)


const form = reactive({
  name: '',
  email: '',
  role: 'Patient',
  doctor: '',
})


function openAddUser() {
  editingUser.value = null

  form.name = ''
  form.email = ''
  form.role = 'Patient'
  form.doctor = ''

  showModal.value = true
}


function editUser(user) {
  editingUser.value = user

  form.name = user.name
  form.email = user.email
  form.role = user.role
  form.doctor = user.doctor

  showModal.value = true
}


function closeModal() {
  showModal.value = false
  editingUser.value = null
}


function saveUser() {
  if (!form.name.trim() || !form.email.trim()) {
    return
  }

  if (editingUser.value) {

    editingUser.value.name = form.name
    editingUser.value.email = form.email
    editingUser.value.role = form.role
    editingUser.value.doctor =
      form.role === 'Patient'
        ? form.doctor
        : ''

  } else {

    users.value.push({
      id: Date.now(),
      name: form.name,
      email: form.email,
      role: form.role,
      status: 'Active',
      doctor:
        form.role === 'Patient'
          ? form.doctor
          : '',
      lastLogin: 'Never',
    })

  }

  closeModal()
}


/* --------------------------------------------------
   Actions
-------------------------------------------------- */

function changeRole(user) {
  const roles = ['Patient', 'Doctor', 'Admin']

  const currentIndex = roles.indexOf(user.role)

  user.role =
    roles[(currentIndex + 1) % roles.length]

  if (user.role !== 'Patient') {
    user.doctor = ''
  }
}


function toggleStatus(user) {
  user.status =
    user.status === 'Active'
      ? 'Inactive'
      : 'Active'
}


/* --------------------------------------------------
   Helpers
-------------------------------------------------- */

function getInitials(name) {
  return name
    .split(' ')
    .map(part => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()
}


function getRoleClass(role) {
  if (role === 'Admin') return 'role-admin'
  if (role === 'Doctor') return 'role-doctor'
  return 'role-patient'
}
</script>


<style scoped>
.users-page {
  width: 100%;
}


/* Header */

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
  line-height: 1.5;
}


/* Buttons */

.primary-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  gap: 8px;

  padding: 11px 17px;

  border: none;
  border-radius: 9px;

  background: #2563eb;

  color: #ffffff;

  font-size: 14px;
  font-weight: 600;

  cursor: pointer;

  white-space: nowrap;
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

  font-size: 14px;
  font-weight: 600;

  cursor: pointer;
}

.secondary-button:hover {
  background: #f8fafc;
}


/* Summary */

.summary-grid {
  display: grid;

  grid-template-columns:
    repeat(4, minmax(0, 1fr));

  gap: 18px;

  margin-bottom: 24px;
}

.summary-card {
  display: flex;
  align-items: center;

  gap: 14px;

  padding: 18px 20px;

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

.summary-icon.orange {
  color: #ea580c;
  background: #fff7ed;
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

  font-size: 23px;
}


/* Users panel */

.users-panel {
  background: #ffffff;

  border: 1px solid #e2e8f0;

  border-radius: 14px;

  overflow: hidden;
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

  font-size: 14px;
}

.search-box input:focus {
  border-color: #93c5fd;
  box-shadow: 0 0 0 3px #eff6ff;
}

.filter-select {
  height: 42px;

  padding: 0 34px 0 12px;

  border: 1px solid #e2e8f0;

  border-radius: 8px;

  background: #ffffff;

  color: #475569;

  font-size: 13px;

  outline: none;

  cursor: pointer;
}


/* Table */

.table-wrapper {
  width: 100%;

  overflow-x: auto;
}

.users-table {
  width: 100%;

  min-width: 900px;

  border-collapse: collapse;
}

.users-table th {
  padding: 14px 20px;

  background: #f8fafc;

  border-bottom: 1px solid #e2e8f0;

  color: #64748b;

  font-size: 11px;
  font-weight: 700;

  text-align: left;

  letter-spacing: 0.7px;
  text-transform: uppercase;
}

.users-table td {
  padding: 16px 20px;

  border-bottom: 1px solid #f1f5f9;

  vertical-align: middle;
}

.users-table tbody tr:hover {
  background: #fafcff;
}


/* User */

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
  font-weight: 600;
}

.user-cell span {
  display: block;

  margin-top: 3px;

  color: #94a3b8;

  font-size: 12px;
}


/* Role */

.role-badge {
  display: inline-flex;

  padding: 6px 9px;

  border-radius: 7px;

  font-size: 12px;
  font-weight: 600;
}

.role-patient {
  background: #eff6ff;
  color: #2563eb;
}

.role-doctor {
  background: #f5f3ff;
  color: #7c3aed;
}

.role-admin {
  background: #fff7ed;
  color: #c2410c;
}


/* Status */

.status-badge {
  display: inline-flex;
  align-items: center;

  gap: 7px;

  font-size: 12px;
  font-weight: 600;
}

.status-badge > span {
  width: 7px;
  height: 7px;

  border-radius: 50%;
}

.status-active {
  color: #15803d;
}

.status-active > span {
  background: #22c55e;
}

.status-inactive {
  color: #94a3b8;
}

.status-inactive > span {
  background: #94a3b8;
}


/* Other table cells */

.doctor-name,
.last-login {
  color: #64748b;

  font-size: 13px;
}

.actions-column {
  text-align: right !important;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;

  gap: 6px;
}

.icon-button {
  width: 34px;
  height: 34px;

  display: inline-flex;
  align-items: center;
  justify-content: center;

  border: 1px solid #e2e8f0;

  border-radius: 7px;

  background: #ffffff;

  color: #64748b;

  cursor: pointer;
}

.icon-button:hover {
  background: #f8fafc;
  color: #2563eb;
}

.activate-button:hover {
  color: #16a34a;
}

.deactivate-button:hover {
  color: #dc2626;
}


/* Empty */

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;

  gap: 6px;

  padding: 60px 20px;

  color: #94a3b8;

  text-align: center;
}

.empty-state strong {
  color: #475569;

  font-size: 14px;
}

.empty-state span {
  font-size: 13px;
}


/* Permission */

.permission-info {
  display: flex;
  align-items: flex-start;

  gap: 14px;

  margin-top: 24px;

  padding: 20px 22px;

  background: #eff6ff;

  border: 1px solid #dbeafe;

  border-radius: 14px;
}

.permission-icon {
  width: 42px;
  height: 42px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  color: #2563eb;

  background: #ffffff;

  border-radius: 10px;
}

.permission-info h3 {
  margin: 0;

  color: #172033;

  font-size: 14px;
  font-weight: 700;
}

.permission-info p {
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
  width: min(540px, 100%);

  padding: 26px;

  background: #ffffff;

  border-radius: 16px;

  box-shadow:
    0 20px 50px rgba(15, 23, 42, 0.18);
}

.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;

  margin-bottom: 25px;
}

.modal-eyebrow {
  margin: 0 0 5px;

  color: #2563eb;

  font-size: 10px;
  font-weight: 700;

  letter-spacing: 1px;
}

.modal-header h2 {
  margin: 0;

  color: #172033;

  font-size: 23px;
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

.close-button:hover {
  background: #f1f5f9;
}

.form-grid {
  display: grid;

  grid-template-columns: 1fr 1fr;

  gap: 18px;
}

.form-grid label {
  display: flex;
  flex-direction: column;

  gap: 7px;
}

.form-grid label span {
  color: #475569;

  font-size: 13px;
  font-weight: 600;
}

.form-grid input,
.form-grid select {
  width: 100%;

  height: 42px;

  padding: 0 12px;

  border: 1px solid #e2e8f0;

  border-radius: 8px;

  outline: none;

  background: #ffffff;

  color: #172033;

  font-size: 14px;
}

.form-grid input:focus,
.form-grid select:focus {
  border-color: #93c5fd;

  box-shadow:
    0 0 0 3px #eff6ff;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;

  gap: 10px;

  margin-top: 26px;

  padding-top: 20px;

  border-top: 1px solid #f1f5f9;
}


/* Responsive */

@media (max-width: 1100px) {
  .summary-grid {
    grid-template-columns:
      repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 700px) {
  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .toolbar {
    align-items: stretch;
    flex-direction: column;
  }

  .search-box {
    max-width: none;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>