<template>
  <div class="login-page">
    <div class="background-shape shape-left"></div>
    <div class="background-shape shape-right"></div>

    <div class="login-container">
      <div class="brand">
        <span class="brand-dot"></span>
        <span>MyMeds</span>
      </div>

      <h1>Welcome to MyMeds</h1>

      <p class="subtitle">
        Sign in to access your medication portal.
      </p>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">
            Email or Username
          </label>

          <input
            id="username"
            v-model="username"
            type="text"
            placeholder="Enter your email or username"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">
            Password
          </label>

          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="Enter your password"
            required
          />
        </div>

        <p
          v-if="error"
          class="error-message"
        >
          Invalid username or password.
        </p>

        <button
          type="submit"
          class="login-button"
          :disabled="loading"
        >
          {{ loading ? 'Logging in...' : 'Log In' }}
        </button>
      </form>

      <p class="authorized-text">
        Authorized users only
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../services/api'

const router = useRouter()

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref(false)

async function handleLogin() {
  loading.value = true
  error.value = false

  try {
    await login(username.value, password.value)

    router.push('/')
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;

  display: flex;
  align-items: center;
  justify-content: center;

  background:
    radial-gradient(
      circle at 25% 35%,
      rgba(163, 211, 255, 0.45),
      transparent 32%
    ),
    radial-gradient(
      circle at 80% 80%,
      rgba(180, 220, 255, 0.45),
      transparent 30%
    ),
    linear-gradient(
      135deg,
      #e8f4ff 0%,
      #f8fbff 50%,
      #eef7ff 100%
    );

  color: #101f3d;
}

.login-container {
  position: relative;
  z-index: 2;

  width: 100%;
  max-width: 520px;

  padding: 40px 32px;

  text-align: center;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;

  margin-bottom: 42px;

  font-size: 28px;
  font-weight: 700;
}

.brand-dot {
  width: 32px;
  height: 32px;

  border-radius: 50%;

  background: #2d6bea;
}

h1 {
  margin: 0;

  font-size: 42px;
  font-weight: 700;
}

.subtitle {
  margin: 14px 0 38px;

  color: #63748f;

  font-size: 20px;
}

form {
  text-align: left;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;

  margin-bottom: 10px;

  font-size: 16px;
  font-weight: 600;
}

.form-group input {
  width: 100%;
  box-sizing: border-box;

  padding: 18px 20px;

  border: 1px solid #dbe3ed;
  border-radius: 14px;

  background: white;

  font-size: 17px;
  outline: none;
}

.form-group input::placeholder {
  color: #9aa8bd;
}

.form-group input:focus {
  border-color: #2d6bea;

  box-shadow: 0 0 0 3px rgba(45, 107, 234, 0.1);
}

.login-button {
  width: 100%;

  margin-top: 8px;

  padding: 18px;

  border: none;
  border-radius: 14px;

  background: #2d66e8;
  color: white;

  font-size: 18px;
  cursor: pointer;
}

.login-button:hover {
  background: #245ad0;
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  margin: -5px 0 18px;

  color: #d83434;

  font-size: 14px;
}

.authorized-text {
  margin-top: 18px;

  color: #63748f;

  font-size: 15px;
}

.background-shape {
  position: absolute;

  border-radius: 50%;

  background: rgba(86, 160, 239, 0.15);
}

.shape-left {
  width: 550px;
  height: 550px;

  left: -280px;
  bottom: -200px;
}

.shape-right {
  width: 430px;
  height: 430px;

  right: -180px;
  bottom: -100px;
}

@media (max-width: 600px) {
  .login-container {
    padding: 30px 24px;
  }

  h1 {
    font-size: 34px;
  }

  .subtitle {
    font-size: 17px;
  }
}
</style>
