<script setup>
import { onMounted, ref } from 'vue'

import { changePassword, getCurrentUser } from '../services/api'

async function loadProfile() {
  loading.value = true
  error.value = ''

  try {
    patient.value = await getCurrentUser()
  } catch (err) {
    console.error('Could not load patient profile:', err)

    error.value = 'Your profile could not be loaded.'
  } finally {
    loading.value = false
  }
}

async function handlePasswordChange() {
  passwordMessage.value = ''
  passwordError.value = ''

  if (!currentPassword.value || !newPassword.value || !confirmPassword.value) {
    passwordError.value = 'Please complete all password fields.'

    return
  }

  if (newPassword.value !== confirmPassword.value) {
    passwordError.value = 'The new passwords do not match.'

    return
  }

  if (newPassword.value.length < 8) {
    passwordError.value = 'The new password must contain at least 8 characters.'

    return
  }

  changingPassword.value = true

  try {
    await changePassword(currentPassword.value, newPassword.value)

    currentPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''

    passwordMessage.value = 'Password changed successfully.'
  } catch (err) {
    console.error('Could not change password:', err)

    passwordError.value = err.message || 'Password could not be changed.'
  } finally {
    changingPassword.value = false
  }
}

const patient = ref(null)

const loading = ref(true)
const error = ref('')

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const changingPassword = ref(false)

const passwordMessage = ref('')
const passwordError = ref('')

onMounted(loadProfile)
</script>

<template>
  <section>
    <h1 class="page-title">My Profile</h1>

    <p class="page-subtitle">Your MyMeds account information.</p>

    <div v-if="loading">
      <p class="page-subtitle">Loading your profile...</p>
    </div>

    <div v-else-if="error" class="success-message">
      {{ error }}
    </div>

    <template v-else-if="patient">
      <div class="profile-header">
        <div class="profile-avatar">👤</div>

        <div>
          <h2>
            {{ patient.name || patient.username }}
          </h2>

          <span class="role-badge">
            {{ patient.role }}
          </span>
        </div>
      </div>

      <div class="profile-card">
        <h3>ACCOUNT INFORMATION</h3>

        <div class="profile-row">
          <span> Username </span>

          <strong>
            {{ patient.username }}
          </strong>
        </div>

        <div class="profile-row">
          <span> Email </span>

          <strong>
            {{ patient.email }}
          </strong>
        </div>

        <div class="profile-row">
          <span> Doctor </span>

          <strong>
            {{ patient.doctor || 'Not assigned' }}
          </strong>
        </div>

        <div class="profile-row">
          <span> Account Status </span>

          <strong>
            {{ patient.active ? 'Active' : 'Inactive' }}
          </strong>
        </div>
      </div>

      <div class="medication-form-card">
        <h2>Change Password</h2>

        <form @submit.prevent="handlePasswordChange">
          <div class="form-content">
            <div class="form-group">
              <label for="current-password"> Current Password </label>

              <input
                id="current-password"
                v-model="currentPassword"
                type="password"
                autocomplete="current-password"
                required
              />
            </div>

            <div class="form-group">
              <label for="new-password"> New Password </label>

              <input
                id="new-password"
                v-model="newPassword"
                type="password"
                autocomplete="new-password"
                minlength="8"
                required
              />
            </div>

            <div class="form-group">
              <label for="confirm-password"> Confirm New Password </label>

              <input
                id="confirm-password"
                v-model="confirmPassword"
                type="password"
                autocomplete="new-password"
                minlength="8"
                required
              />
            </div>
          </div>

          <div class="form-footer">
            <button type="submit" class="primary-button" :disabled="changingPassword">
              {{ changingPassword ? 'Changing...' : 'Change Password' }}
            </button>
          </div>
        </form>

        <div v-if="passwordMessage" class="success-message">
          {{ passwordMessage }}
        </div>

        <div v-if="passwordError" class="success-message">
          {{ passwordError }}
        </div>
      </div>
    </template>
  </section>
</template>
