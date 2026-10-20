<template>
  <div class="users-page">

    <!-- Header -->

    <header class="page-header">
      <div>

        <p class="eyebrow">
          ADMINISTRATION
        </p>

        <h1 class="page-title">
          Users
        </h1>

        <p class="page-subtitle">
          Manage MyMeds user accounts, roles, and account status.
        </p>

      </div>


      <button
        class="primary-button"
        type="button"
        @click="openAddUser"
      >
        <UserPlus :size="18" />

        Add User
      </button>

    </header>


    <!-- Error -->

    <div
      v-if="errorMessage"
      class="error-box"
    >
      <CircleAlert :size="20" />

      <div>
        <strong>
          Unable to load users
        </strong>

        <p>
          {{ errorMessage }}
        </p>
      </div>

      <button
        type="button"
        class="retry-button"
        @click="loadUsers"
      >
        Retry
      </button>
    </div>


    <!-- Panel -->

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


        <select
          v-model="roleFilter"
          class="filter-select"
        >
          <option value="All">
            All roles
          </option>

          <option value="Admin">
            Admin
          </option>

          <option value="Doctor">
            Doctor
          </option>

          <option value="Patient">
            Patient
          </option>
        </select>


        <select
          v-model="statusFilter"
          class="filter-select"
        >
          <option value="All">
            All statuses
          </option>

          <option value="Active">
            Active
          </option>

          <option value="Inactive">
            Inactive
          </option>
        </select>

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
          Loading users...
        </strong>
      </div>


      <!-- Table -->

      <div
        v-else
        class="table-wrapper"
      >

        <table class="users-table">

          <thead>
            <tr>

              <th>
                User
              </th>

              <th>
                Role
              </th>

              <th>
                Status
              </th>

              <th class="actions-column">
                Actions
              </th>

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

                    <strong>
                      {{ user.name }}
                    </strong>

                    <span>
                      {{ user.email || 'No email available' }}
                    </span>

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
                  :class="
                    user.status === 'Active'
                      ? 'status-active'
                      : 'status-inactive'
                  "
                >

                  <span></span>

                  {{ user.status }}

                </span>

              </td>


              <!-- Actions -->

              <td class="actions-column">

                <div class="action-buttons">

                  <button
                    class="icon-button"
                    type="button"
                    title="Edit user"
                    :disabled="saving"
                    @click="editUser(user)"
                  >
                    <Pencil :size="17" />
                  </button>


                  <button
                    class="icon-button"
                    :class="
                      user.status === 'Active'
                        ? 'deactivate-button'
                        : 'activate-button'
                    "
                    type="button"
                    :disabled="saving"
                    :title="
                      user.status === 'Active'
                        ? 'Deactivate user'
                        : 'Activate user'
                    "
                    @click="toggleStatus(user)"
                  >

                    <UserX
                      v-if="user.status === 'Active'"
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


            <!-- Empty -->

            <tr
              v-if="
                !errorMessage &&
                filteredUsers.length === 0
              "
            >

              <td colspan="4">

                <div class="empty-state">

                  <Users :size="30" />

                  <strong>
                    No users available
                  </strong>

                  <span v-if="users.length === 0">
                    No user accounts were returned by the backend.
                  </span>

                  <span v-else>
                    No users match the current filters.
                  </span>

                </div>

              </td>

            </tr>

          </tbody>

        </table>

      </div>

    </section>


    <!-- Information -->

    <section class="permission-info">

      <div class="permission-icon">
        <ShieldCheck :size="21" />
      </div>


      <div>

        <h3>
          Account administration
        </h3>

        <p>
          Administrators manage user accounts, roles, and account
          status. Doctor-patient assignments are managed separately.
          Medication prescribing and clinical decisions remain under
          doctor control.
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
              {{
                editingUser
                  ? 'EDIT USER'
                  : 'NEW USER'
              }}
            </p>

            <h2>
              {{
                editingUser
                  ? 'Edit User'
                  : 'Add User'
              }}
            </h2>

          </div>


          <button
            class="close-button"
            type="button"
            :disabled="saving"
            @click="closeModal"
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


        <div class="form-grid">

          <label>

            <span>
              Full Name
            </span>

            <input
              v-model="form.name"
              type="text"
              placeholder="Enter full name"
            />

          </label>


          <label>

            <span>
              Email Address
            </span>

            <input
              v-model="form.email"
              type="email"
              placeholder="Enter email address"
            />

          </label>


          <label>

            <span>
              Role
            </span>

            <select v-model="form.role">

              <option value="Patient">
                Patient
              </option>

              <option value="Doctor">
                Doctor
              </option>

              <option value="Admin">
                Admin
              </option>

            </select>

          </label>

        </div>


        <div class="modal-actions">

          <button
            class="secondary-button"
            type="button"
            :disabled="saving"
            @click="closeModal"
          >
            Cancel
          </button>


          <button
            class="primary-button"
            type="button"
            :disabled="saving"
            @click="saveUser"
          >
            {{
              saving
                ? 'Saving...'
                : editingUser
                  ? 'Save Changes'
                  : 'Create User'
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
  onMounted,
  reactive,
  ref,
} from 'vue'

import {
  CircleAlert,
  LoaderCircle,
  Pencil,
  Search,
  ShieldCheck,
  UserCheck,
  UserPlus,
  Users,
  UserX,
  X,
} from '@lucide/vue'

import {
  getAdminUsers,
  registerUser,
  updateAdminUser,
  updateAdminUserRole,
  updateAdminUserStatus,
} from '../services/api'


// =========================================================
// STATE
// =========================================================

const users = ref([])

const loading = ref(false)
const saving = ref(false)

const errorMessage = ref('')
const modalError = ref('')

const searchQuery = ref('')
const roleFilter = ref('All')
const statusFilter = ref('All')

const showModal = ref(false)
const editingUser = ref(null)

const originalRole = ref(null)


const form = reactive({
  name: '',
  email: '',
  role: 'Patient',
})


// =========================================================
// LOAD USERS
// =========================================================

async function loadUsers() {
  loading.value = true
  errorMessage.value = ''

  try {
    const result = await getAdminUsers()

    const backendUsers =
      Array.isArray(result)
        ? result
        : Array.isArray(result?.content)
          ? result.content
          : Array.isArray(result?.users)
            ? result.users
            : []


    users.value =
      backendUsers.map(normalizeUser)

  } catch (error) {
    console.error(
      'Failed to load users:',
      error
    )

    users.value = []

    errorMessage.value =
      getErrorMessage(error)

  } finally {
    loading.value = false
  }
}


// =========================================================
// NORMALIZE BACKEND USER
// =========================================================

function normalizeUser(user) {
  const rawRole =
    user.role ??
    user.authority ??
    user.userRole ??
    'PATIENT'


  const normalizedRole =
    String(rawRole)
      .toUpperCase()
      .replace(/^ROLE_/, '')


  let role = 'Patient'

  if (normalizedRole === 'ADMIN') {
    role = 'Admin'
  }

  if (normalizedRole === 'DOCTOR') {
    role = 'Doctor'
  }


  const active =
    user.active ??
    user.enabled ??
    user.isActive


  const status =
    active === false ||
    String(user.status || '')
      .toLowerCase() === 'inactive'
      ? 'Inactive'
      : 'Active'


  return {
    id:
      user.id ??
      user.userId ??
      user.username,

    name:
      user.name ??
      user.fullName ??
      user.username ??
      'Unnamed user',

    email:
      user.email ??
      '',

    role,

    status,
  }
}


// =========================================================
// FILTERING
// =========================================================

const filteredUsers = computed(() => {
  const query =
    searchQuery.value
      .trim()
      .toLowerCase()


  return users.value.filter((user) => {

    const matchesSearch =
      !query ||
      user.name
        .toLowerCase()
        .includes(query) ||
      user.email
        .toLowerCase()
        .includes(query)


    const matchesRole =
      roleFilter.value === 'All' ||
      user.role === roleFilter.value


    const matchesStatus =
      statusFilter.value === 'All' ||
      user.status === statusFilter.value


    return (
      matchesSearch &&
      matchesRole &&
      matchesStatus
    )
  })
})


// =========================================================
// MODAL
// =========================================================

function openAddUser() {
  editingUser.value = null
  originalRole.value = null

  modalError.value = ''

  form.name = ''
  form.email = ''
  form.role = 'Patient'

  showModal.value = true
}


function editUser(user) {
  editingUser.value = user

  originalRole.value = user.role

  modalError.value = ''

  form.name = user.name
  form.email = user.email
  form.role = user.role

  showModal.value = true
}


function closeModal() {
  if (saving.value) {
    return
  }

  showModal.value = false

  editingUser.value = null
  originalRole.value = null

  modalError.value = ''
}


// =========================================================
// SAVE USER
// =========================================================

async function saveUser() {
  if (
    !form.name.trim() ||
    !form.email.trim()
  ) {
    modalError.value =
      'Name and email are required.'

    return
  }


  saving.value = true
  modalError.value = ''


  try {

    if (editingUser.value) {

      const id = editingUser.value.id


      await updateAdminUser(id, {
        name: form.name.trim(),
        email: form.email.trim(),
      })


      if (
        form.role !==
        originalRole.value
      ) {
        await updateAdminUserRole(
          id,
          form.role.toUpperCase()
        )
      }

    } else {

      await registerUser({
        name: form.name.trim(),
        email: form.email.trim(),
        role: form.role.toUpperCase(),
      })

    }


    showModal.value = false

    editingUser.value = null
    originalRole.value = null

    await loadUsers()

  } catch (error) {
    console.error(
      'Failed to save user:',
      error
    )

    modalError.value =
      getErrorMessage(error)

  } finally {
    saving.value = false
  }
}


// =========================================================
// STATUS
// =========================================================

async function toggleStatus(user) {
  if (saving.value) {
    return
  }


  saving.value = true
  errorMessage.value = ''


  try {
    const shouldBeActive =
      user.status !== 'Active'


    await updateAdminUserStatus(
      user.id,
      shouldBeActive
    )


    await loadUsers()

  } catch (error) {
    console.error(
      'Failed to update user status:',
      error
    )

    errorMessage.value =
      getErrorMessage(error)

  } finally {
    saving.value = false
  }
}


// =========================================================
// HELPERS
// =========================================================

function getInitials(name) {
  return String(name || '')
    .split(' ')
    .filter(Boolean)
    .map(part => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()
}


function getRoleClass(role) {
  if (role === 'Admin') {
    return 'role-admin'
  }

  if (role === 'Doctor') {
    return 'role-doctor'
  }

  return 'role-patient'
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


// =========================================================
// START
// =========================================================

onMounted(loadUsers)
</script>


<style scoped>
.users-page {
  width: 100%;
}


/* =========================================================
   HEADER
========================================================= */

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


/* =========================================================
   BUTTONS
========================================================= */

.primary-button,
.secondary-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  gap: 8px;

  padding: 11px 17px;

  border-radius: 9px;

  font-size: 14px;
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


.secondary-button {
  border: 1px solid #e2e8f0;

  background: #ffffff;

  color: #475569;
}


.secondary-button:hover:not(:disabled) {
  background: #f8fafc;
}


.primary-button:disabled,
.secondary-button:disabled {
  opacity: 0.55;

  cursor: not-allowed;
}


/* =========================================================
   ERROR
========================================================= */

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


/* =========================================================
   PANEL
========================================================= */

.users-panel {
  overflow: hidden;

  border: 1px solid #e2e8f0;
  border-radius: 14px;

  background: #ffffff;
}


/* =========================================================
   TOOLBAR
========================================================= */

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

  background: #ffffff;

  color: #172033;

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

  outline: none;

  background: #ffffff;

  color: #475569;

  font-size: 13px;
}


/* =========================================================
   LOADING
========================================================= */

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


/* =========================================================
   TABLE
========================================================= */

.table-wrapper {
  width: 100%;

  overflow-x: auto;
}


.users-table {
  width: 100%;

  min-width: 700px;

  border-collapse: collapse;
}


.users-table th {
  padding: 14px 20px;

  border-bottom: 1px solid #e2e8f0;

  background: #f8fafc;

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


/* =========================================================
   USER
========================================================= */

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


/* =========================================================
   ROLE
========================================================= */

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


/* =========================================================
   STATUS
========================================================= */

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


/* =========================================================
   ACTIONS
========================================================= */

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


.icon-button:hover:not(:disabled) {
  background: #f8fafc;

  color: #2563eb;
}


.icon-button:disabled {
  opacity: 0.45;

  cursor: not-allowed;
}


.activate-button:hover:not(:disabled) {
  color: #16a34a;
}


.deactivate-button:hover:not(:disabled) {
  color: #dc2626;
}


/* =========================================================
   EMPTY
========================================================= */

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


/* =========================================================
   PERMISSIONS
========================================================= */

.permission-info {
  display: flex;
  align-items: flex-start;

  gap: 14px;

  margin-top: 24px;

  padding: 20px 22px;

  border: 1px solid #dbeafe;
  border-radius: 14px;

  background: #eff6ff;
}


.permission-icon {
  width: 42px;
  height: 42px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 10px;

  background: #ffffff;

  color: #2563eb;
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


/* =========================================================
   MODAL
========================================================= */

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

  border-radius: 16px;

  background: #ffffff;

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

  box-shadow: 0 0 0 3px #eff6ff;
}


.modal-actions {
  display: flex;
  justify-content: flex-end;

  gap: 10px;

  margin-top: 26px;

  padding-top: 20px;

  border-top: 1px solid #f1f5f9;
}


/* =========================================================
   RESPONSIVE
========================================================= */

@media (max-width: 700px) {

  .page-header {
    align-items: flex-start;

    flex-direction: column;
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
