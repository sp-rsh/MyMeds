<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

import {
  getAdminUsers,
  registerUser,
  updateAdminUser,
  updateAdminUserStatus,
} from '../services/api'

async function loadUsers() {
  loading.value = true
  error.value = ''

  try {
    users.value = await getAdminUsers()
  } catch (err) {
    console.error('Could not load users:', err)

    error.value = 'Users could not be loaded.'
  } finally {
    loading.value = false
  }
}

function openAddUser() {
  editingUser.value = null

  form.username = ''
  form.email = ''
  form.password = ''
  form.role = 'PATIENT'

  formError.value = ''
  showForm.value = true
}

function openEditUser(user) {
  editingUser.value = user

  form.username = user.username || ''
  form.email = user.email || ''
  form.password = ''
  form.role = user.role || 'PATIENT'

  formError.value = ''
  showForm.value = true
}

function closeForm() {
  showForm.value = false
  editingUser.value = null
  formError.value = ''
}

async function saveUser() {
  formError.value = ''

  if (!form.username.trim() || !form.email.trim()) {
    formError.value = 'Username and email are required.'

    return
  }

  if (!editingUser.value && form.password.length < 8) {
    formError.value = 'Password must contain at least 8 characters.'

    return
  }

  if (editingUser.value && form.password && form.password.length < 8) {
    formError.value = 'New password must contain at least 8 characters.'

    return
  }

  saving.value = true

  try {
    if (editingUser.value) {
      const user = {
        username: form.username.trim(),
        email: form.email.trim(),
        role: form.role,
      }

      if (form.password) {
        user.password = form.password
      }

      await updateAdminUser(editingUser.value.id, user)
    } else {
      await registerUser({
        username: form.username.trim(),
        email: form.email.trim(),
        password: form.password,
        role: form.role,
      })
    }

    closeForm()
    await loadUsers()
  } catch (err) {
    console.error('Could not save user:', err)

    formError.value = err.message || 'User could not be saved.'
  } finally {
    saving.value = false
  }
}

async function toggleStatus(user) {
  error.value = ''

  try {
    await updateAdminUserStatus(user.id, !user.active)

    await loadUsers()
  } catch (err) {
    console.error('Could not update user status:', err)

    error.value = err.message || 'Account status could not be changed.'
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

const users = ref([])

const loading = ref(true)
const saving = ref(false)

const error = ref('')
const formError = ref('')

const search = ref('')

const showForm = ref(false)
const editingUser = ref(null)

const form = reactive({
  username: '',
  email: '',
  password: '',
  role: 'PATIENT',
})

const filteredUsers = computed(() => {
  const value = search.value.trim().toLowerCase()

  if (!value) {
    return users.value
  }

  return users.value.filter(
    (user) =>
      user.username?.toLowerCase().includes(value) ||
      user.email?.toLowerCase().includes(value) ||
      user.role?.toLowerCase().includes(value),
  )
})

onMounted(loadUsers)
</script>

<template>
  <section>
    <h1 class="page-title">Users</h1>

    <p class="page-subtitle">Create and manage MyMeds user accounts.</p>

    <div class="patients-toolbar">
      <button type="button" class="primary-button" @click="openAddUser">Add User</button>

      <input v-model="search" class="search-input" type="text" placeholder="Search users..." />
    </div>

    <p v-if="loading" class="loading-message">Loading users...</p>

    <p v-else-if="error" class="error-message">
      {{ error }}
    </p>

    <div v-else class="table-card">
      <table>
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Last Login</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td class="patient-name">
              {{ user.username }}
            </td>

            <td>
              {{ user.email }}
            </td>

            <td>
              {{ user.role }}
            </td>

            <td>
              <span class="status-badge" :class="user.active ? 'taken' : 'missed'">
                <span class="status-dot"></span>

                {{ user.active ? 'Active' : 'Inactive' }}
              </span>
            </td>

            <td>
              {{ formatLastLogin(user.lastLoginAt) }}
            </td>

            <td>
              <div class="action-cell">
                <button type="button" class="primary-button" @click="openEditUser(user)">
                  Edit
                </button>

                <button type="button" class="secondary-button" @click="toggleStatus(user)">
                  {{ user.active ? 'Deactivate' : 'Activate' }}
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="filteredUsers.length === 0">
            <td colspan="6">No users found.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showForm" class="medication-form-card">
      <h2>
        {{ editingUser ? 'Edit User' : 'Add User' }}
      </h2>

      <form @submit.prevent="saveUser">
        <div class="form-content">
          <div class="form-group">
            <label for="username"> Username </label>

            <input id="username" v-model="form.username" type="text" required />
          </div>

          <div class="form-group">
            <label for="email"> Email </label>

            <input id="email" v-model="form.email" type="email" required />
          </div>

          <div class="form-group">
            <label for="role"> Role </label>

            <select id="role" v-model="form.role">
              <option value="PATIENT">Patient</option>

              <option value="DOCTOR">Doctor</option>

              <option value="ADMIN">Admin</option>
            </select>
          </div>

          <div class="form-group">
            <label for="password">
              {{ editingUser ? 'New Password' : 'Password' }}
            </label>

            <input
              id="password"
              v-model="form.password"
              type="password"
              :required="!editingUser"
              minlength="8"
            />

            <p v-if="editingUser" class="page-subtitle">
              Leave blank to keep the current password.
            </p>
          </div>

          <p v-if="formError" class="error-message">
            {{ formError }}
          </p>
        </div>

        <div class="form-footer">
          <div class="form-actions">
            <button type="submit" class="primary-button" :disabled="saving">
              {{ saving ? 'Saving...' : editingUser ? 'Save Changes' : 'Create User' }}
            </button>

            <button type="button" class="secondary-button" @click="closeForm">Cancel</button>
          </div>
        </div>
      </form>
    </div>
  </section>
</template>
