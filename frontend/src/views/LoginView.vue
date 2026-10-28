<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import QRCode from 'qrcode'

import {
  login,
  loginWithTotp,
  setupTotp,
  getCurrentUser,
  verifyTotpSetup,
} from '../services/api'

const router = useRouter()

const username = ref('')
const password = ref('')
const totpCode = ref('')
const totpSecret = ref('')
const totpUri = ref('')
const qrCodeDataUrl = ref('')
const totpRequired = ref(false)
const totpSetupRequired = ref(false)
const loading = ref(false)
const error = ref('')

function goToRole(user) {
  const role = String(user?.role || '')
    .toUpperCase()
    .replace(/^ROLE_/, '')

  if (role === 'DOCTOR') {
    return router.push('/doctor')
  }

  if (role === 'PATIENT') {
    return router.push('/patient')
  }

  if (role === 'ADMIN') {
    return router.push('/admin/users')
  }

  return router.push('/unauthorized')
}

async function handleLogin() {
  loading.value = true
  error.value = ''

  try {
    const result = await login(username.value, password.value)

    if (result.totpRequired) {
      totpRequired.value = true
      return
    }

    if (result.totpSetupRequired) {
      totpSetupRequired.value = true
      const setup = await setupTotp()
      totpSecret.value = setup.secret
      totpUri.value = setup.otpauthUri
      qrCodeDataUrl.value = await QRCode.toDataURL(setup.otpauthUri, {
        errorCorrectionLevel: 'M',
        margin: 2,
        width: 220,
      })
      return
    }

    await goToRole(result)
  } catch (err) {
    console.error('Login failed:', err)

    error.value = 'Invalid username or password.'
  } finally {
    loading.value = false
  }
}

async function handleTotpLogin() {
  loading.value = true
  error.value = ''

  try {
    const user = await loginWithTotp(totpCode.value)
    await goToRole(user)
  } catch (err) {
    console.error('TOTP login failed:', err)
    error.value = err.message || 'Invalid authenticator code.'
  } finally {
    loading.value = false
  }
}

async function handleTotpSetup() {
  loading.value = true
  error.value = ''

  try {
    await verifyTotpSetup(totpCode.value)
    const user = await getCurrentUser()
    await goToRole(user)
  } catch (err) {
    console.error('TOTP setup failed:', err)
    error.value = err.message || 'Could not enable two-factor authentication.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="background-shape shape-left"></div>
    <div class="background-shape shape-right"></div>

    <div class="login-container">
      <div class="login-brand">
        <span class="login-brand-dot"></span>

        <span> MyMeds </span>
      </div>

      <h1 class="login-title">Welcome to MyMeds</h1>

      <p class="login-subtitle">
        {{
          totpRequired
            ? 'Enter the code from your authenticator app.'
            : totpSetupRequired
              ? 'Two-factor authentication is required. Add it to continue.'
              : 'Sign in to access your medication portal.'
        }}
      </p>

      <form
        v-if="!totpRequired && !totpSetupRequired"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <div class="login-form-group">
          <label for="username"> Email or Username </label>

          <input
            id="username"
            v-model="username"
            type="text"
            placeholder="Enter your email or username"
            autocomplete="username"
            required
          />
        </div>

        <div class="login-form-group">
          <label for="password"> Password </label>

          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="Enter your password"
            autocomplete="current-password"
            required
          />
        </div>

        <p v-if="error" class="login-error-message">
          {{ error }}
        </p>

        <button type="submit" class="login-button" :disabled="loading">
          {{ loading ? 'Logging in...' : 'Log In' }}
        </button>
      </form>

      <form
        v-else
        class="login-form"
        @submit.prevent="totpRequired ? handleTotpLogin() : handleTotpSetup()"
      >
        <div v-if="totpSetupRequired" class="login-form-group">
          <p>
            Scan this QR code in your authenticator app:
          </p>
          <img
            v-if="qrCodeDataUrl"
            class="totp-qr-code"
            :src="qrCodeDataUrl"
            alt="TOTP setup QR code"
          />
          <p>
            If you cannot scan a QR code, enter this secret manually:
          </p>
          <code>{{ totpSecret }}</code>
        </div>

        <div class="login-form-group">
          <label for="totp-code">
            {{ totpSetupRequired ? 'Setup code' : 'Authenticator code' }}
          </label>
          <input
            id="totp-code"
            v-model="totpCode"
            type="text"
            inputmode="numeric"
            autocomplete="one-time-code"
            pattern="[0-9]{6}"
            maxlength="6"
            placeholder="123456"
            required
          />
        </div>

        <p v-if="error" class="login-error-message">
          {{ error }}
        </p>

        <button type="submit" class="login-button" :disabled="loading">
          {{
            loading
              ? 'Please wait...'
              : totpSetupRequired
                ? 'Enable 2FA'
                : 'Verify code'
          }}
        </button>
      </form>

      <p class="authorized-text">Authorized users only</p>
    </div>
  </div>
</template>
