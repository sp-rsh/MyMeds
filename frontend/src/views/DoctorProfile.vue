<script setup>
import { onMounted, ref } from 'vue'

import { changePassword, getCurrentDoctor } from '../services/api'

async function loadDoctor() {
  loading.value = true
  error.value = ''

  try {
    doctor.value = await getCurrentDoctor()
  } catch (err) {
    console.error('Could not load doctor profile:', err)

    error.value = 'Your profile could not be loaded.'
  } finally {
    loading.value = false
  }
}

async function handlePasswordChange() {
  passwordError.value = ''
  passwordMessage.value = ''

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

const doctor = ref(null)

const loading = ref(true)
const error = ref('')

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const changingPassword = ref(false)

const passwordMessage = ref('')
const passwordError = ref('')

onMounted(loadDoctor)
</script>

<template>
  <section>
    <h1 class="page-title">My Profile</h1>

    <p class="page-subtitle">Doctor account information.</p>

    <p v-if="loading" class="loading-message">Loading profile...</p>

    <p v-else-if="error" class="error-message">
      {{ error }}
    </p>

    <template v-else-if="doctor">
      <div class="profile-header">
        <div class="profile-avatar">👤</div>

        <div>
          <h2>
            {{ doctor.name }}
          </h2>

          <span class="role-badge">
            {{ doctor.role }}
          </span>
        </div>
      </div>

      <div class="profile-card">
        <h3>PROFESSIONAL INFORMATION</h3>

        <div class="profile-row">
          <span> Name </span>

          <strong>
            {{ doctor.name }}
          </strong>
        </div>

        <div class="profile-row">
          <span> Role </span>

          <strong>
            {{ doctor.role }}
          </strong>
        </div>

        <div class="profile-row">
          <span> Email </span>

          <strong>
            {{ doctor.email }}
          </strong>
        </div>
      </div>

      <div class="medication-form-card">
        <h2>Change Password</h2>

        <form @submit.prevent="handlePasswordChange">
          <div class="form-content">
            <div class="form-group">
              <label for="doctor-current-password"> Current Password </label>

              <input
                id="doctor-current-password"
                v-model="currentPassword"
                type="password"
                autocomplete="current-password"
                required
              />
            </div>

            <div class="form-group">
              <label for="doctor-new-password"> New Password </label>

              <input
                id="doctor-new-password"
                v-model="newPassword"
                type="password"
                autocomplete="new-password"
                minlength="8"
                required
              />
            </div>

            <div class="form-group">
              <label for="doctor-confirm-password"> Confirm New Password </label>

              <input
                id="doctor-confirm-password"
                v-model="confirmPassword"
                type="password"
                autocomplete="new-password"
                minlength="8"
                required
              />
            </div>

            <p v-if="passwordError" class="error-message">
              {{ passwordError }}
            </p>

            <p v-if="passwordMessage" class="success-message">
              {{ passwordMessage }}
            </p>
          </div>

          <div class="form-footer">
            <button type="submit" class="primary-button" :disabled="changingPassword">
              {{ changingPassword ? 'Changing...' : 'Change Password' }}
            </button>
          </div>
        </form>
      </div>
    </template>
  </section>
</template>
