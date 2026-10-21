<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { login } from '../services/api'

const router = useRouter()

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  loading.value = true
  error.value = ''

  try {
    const user = await login(
      username.value,
      password.value
    )

    const role = String(
      user?.role || ''
    )
      .toUpperCase()
      .replace(/^ROLE_/, '')

    if (role === 'DOCTOR') {
      await router.push('/doctor')
      return
    }

    if (role === 'PATIENT') {
      await router.push('/patient')
      return
    }

    if (role === 'ADMIN') {
      await router.push('/admin/users')
      return
    }

    await router.push('/unauthorized')
  } catch (err) {
    console.error(
      'Login failed:',
      err
    )

    error.value =
      'Invalid username or password.'
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

        <span>
          MyMeds
        </span>
      </div>

      <h1 class="login-title">
        Welcome to MyMeds
      </h1>

      <p class="login-subtitle">
        Sign in to access your medication portal.
      </p>

      <form
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <div class="login-form-group">
          <label for="username">
            Email or Username
          </label>

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
          <label for="password">
            Password
          </label>

          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="Enter your password"
            autocomplete="current-password"
            required
          />
        </div>

        <p
          v-if="error"
          class="login-error-message"
        >
          {{ error }}
        </p>

        <button
          type="submit"
          class="login-button"
          :disabled="loading"
        >
          {{
            loading
              ? 'Logging in...'
              : 'Log In'
          }}
        </button>
      </form>

      <p class="authorized-text">
        Authorized users only
      </p>
    </div>
  </div>
</template>
