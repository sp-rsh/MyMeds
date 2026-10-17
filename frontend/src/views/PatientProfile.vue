<script setup>
import { ref } from 'vue'
import {
  UserCircle,
  Stethoscope,
  Fingerprint,
  ShieldCheck,
  LockKeyhole,
  LogOut,
  CheckCircle2,
  Eye,
  EyeOff,
} from '@lucide/vue'

const showPassword = ref(false)
const showChangePassword = ref(false)

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const passwordMessage = ref('')

function changePassword() {
  passwordMessage.value = ''

  if (
    !currentPassword.value ||
    !newPassword.value ||
    !confirmPassword.value
  ) {
    passwordMessage.value = 'Please complete all password fields.'
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    passwordMessage.value = 'New passwords do not match.'
    return
  }

  passwordMessage.value = 'Password changed successfully.'

  currentPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
}
</script>

<template>
  <section class="profile-page">

    <!-- Page Header -->
    <header class="page-header">
      <h1 class="page-title">My Profile</h1>

      <p class="page-subtitle">
        View your account information and security settings.
      </p>
    </header>

    <!-- Main Profile Area -->
    <div class="profile-layout">

      <!-- Profile Information -->
      <article class="profile-card">

        <div class="card-header">
          <div class="profile-avatar">
            <UserCircle :size="34" />
          </div>

          <div>
            <h2>Anvi Chahal</h2>
            <p>Patient</p>
          </div>
        </div>

        <div class="divider"></div>

        <div class="profile-details">

          <div class="detail-row">
            <div class="detail-icon">
              <UserCircle :size="19" />
            </div>

            <div class="detail-content">
              <span class="detail-label">Name</span>
              <strong>Anvi Chahal</strong>
            </div>
          </div>

          <div class="detail-row">
            <div class="detail-icon">
              <Stethoscope :size="19" />
            </div>

            <div class="detail-content">
              <span class="detail-label">Doctor</span>
              <strong>Dr. Sharma</strong>
            </div>
          </div>

          <div class="detail-row">
            <div class="detail-icon">
              <Fingerprint :size="19" />
            </div>

            <div class="detail-content">
              <span class="detail-label">Fingerprint</span>

              <strong class="verified">
                <CheckCircle2 :size="16" />
                Registered
              </strong>
            </div>
          </div>

          <div class="detail-row">
            <div class="detail-icon">
              <ShieldCheck :size="19" />
            </div>

            <div class="detail-content">
              <span class="detail-label">
                Two-Factor Authentication
              </span>

              <strong class="verified">
                <CheckCircle2 :size="16" />
                Enabled
              </strong>
            </div>
          </div>

        </div>
      </article>

      <!-- Security -->
      <article class="security-card">

        <div class="card-title-row">
          <div class="section-icon">
            <LockKeyhole :size="21" />
          </div>

          <div>
            <h2>Security</h2>
            <p>Manage your account security.</p>
          </div>
        </div>

        <!-- Password -->
        <div class="security-item">

          <div class="password-section">
            <span class="detail-label">Password</span>

            <div class="password-display">
              <span>
                {{ showPassword ? 'MyMedsPassword' : '••••••••••••' }}
              </span>

              <button
                type="button"
                class="icon-button"
                aria-label="Toggle password visibility"
                @click="showPassword = !showPassword"
              >
                <EyeOff
                  v-if="showPassword"
                  :size="17"
                />

                <Eye
                  v-else
                  :size="17"
                />
              </button>
            </div>
          </div>

          <button
            type="button"
            class="secondary-button"
            @click="showChangePassword = !showChangePassword"
          >
            {{ showChangePassword ? 'Cancel' : 'Change Password' }}
          </button>

        </div>

        <!-- Change Password Form -->
        <div
          v-if="showChangePassword"
          class="password-form"
        >

          <div class="form-group">
            <label for="current-password">
              Current Password
            </label>

            <input
              id="current-password"
              v-model="currentPassword"
              type="password"
              placeholder="Enter current password"
            />
          </div>

          <div class="form-group">
            <label for="new-password">
              New Password
            </label>

            <input
              id="new-password"
              v-model="newPassword"
              type="password"
              placeholder="Enter new password"
            />
          </div>

          <div class="form-group">
            <label for="confirm-password">
              Confirm New Password
            </label>

            <input
              id="confirm-password"
              v-model="confirmPassword"
              type="password"
              placeholder="Confirm new password"
            />
          </div>

          <button
            type="button"
            class="save-button"
            @click="changePassword"
          >
            Save Password
          </button>

          <p
            v-if="passwordMessage"
            class="password-message"
          >
            {{ passwordMessage }}
          </p>

        </div>

      </article>

    </div>

    <!-- Security Information -->
    <div class="security-info">

      <ShieldCheck :size="20" />

      <div>
        <strong>Your account is protected</strong>

        <p>
          Two-factor authentication and fingerprint authentication help
          protect your MyMeds account and medication information.
        </p>
      </div>

    </div>

    <!-- Logout -->
    <button
      type="button"
      class="logout-button"
    >
      <LogOut :size="19" />
      Log out
    </button>

  </section>
</template>

<style scoped>
.profile-page {
  max-width: 1200px;
}

.page-header {
  margin-bottom: 32px;
}

.profile-layout {
  display: grid;
  grid-template-columns: 1.15fr 1fr;
  gap: 24px;
}

.profile-card,
.security-card {
  padding: 28px;
  background: #ffffff;
  border: 1px solid #e0e6ee;
  border-radius: 18px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 15px;
}

.profile-avatar {
  width: 58px;
  height: 58px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 16px;

  background: #eaf2ff;
  color: #2563eb;
}

.card-header h2 {
  margin: 0;
  color: #172033;
  font-size: 22px;
  font-weight: 700;
}

.card-header p {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 14px;
}

.divider {
  height: 1px;
  margin: 26px 0;
  background: #e0e6ee;
}

.profile-details {
  display: flex;
  flex-direction: column;
  gap: 21px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 13px;
}

.detail-icon {
  width: 40px;
  height: 40px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border: 1px solid #e0e6ee;
  border-radius: 10px;

  background: #f8fafc;
  color: #475569;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  color: #94a3b8;
  font-size: 12px;
}

.detail-row strong {
  display: flex;
  align-items: center;
  gap: 5px;

  color: #172033;
  font-size: 15px;
  font-weight: 600;
}

.verified {
  color: #15803d !important;
}

.card-title-row {
  display: flex;
  align-items: center;
  gap: 13px;
  margin-bottom: 28px;
}

.section-icon {
  width: 44px;
  height: 44px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 12px;

  background: #eaf2ff;
  color: #2563eb;
}

.card-title-row h2 {
  margin: 0;
  color: #172033;
  font-size: 20px;
  font-weight: 700;
}

.card-title-row p {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 14px;
}

.security-item {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;

  padding: 18px;

  border: 1px solid #e0e6ee;
  border-radius: 13px;

  background: #fafbfc;
}

.password-section {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.password-display {
  display: flex;
  align-items: center;
  gap: 10px;

  color: #172033;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
}

.icon-button {
  width: 30px;
  height: 30px;

  display: flex;
  align-items: center;
  justify-content: center;

  border: 0;
  border-radius: 8px;

  background: transparent;
  color: #64748b;

  cursor: pointer;
}

.icon-button:hover {
  background: #eaf2ff;
  color: #2563eb;
}

.secondary-button {
  padding: 9px 13px;

  border: 1px solid #dbe3ee;
  border-radius: 9px;

  background: #ffffff;
  color: #2563eb;

  cursor: pointer;

  font-size: 13px;
  font-weight: 600;
}

.secondary-button:hover {
  background: #f8fbff;
}

.password-form {
  display: flex;
  flex-direction: column;
  gap: 16px;

  margin-top: 18px;
  padding-top: 18px;

  border-top: 1px solid #e0e6ee;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.form-group label {
  color: #475569;
  font-size: 13px;
  font-weight: 600;
}

.form-group input {
  width: 100%;
  padding: 11px 12px;

  border: 1px solid #dbe3ee;
  border-radius: 9px;

  outline: none;

  background: #ffffff;
  color: #172033;

  font-size: 14px;
}

.form-group input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px #eaf2ff;
}

.save-button {
  align-self: flex-start;

  padding: 10px 15px;

  border: 0;
  border-radius: 9px;

  background: #2563eb;
  color: #ffffff;

  cursor: pointer;

  font-size: 13px;
  font-weight: 600;
}

.save-button:hover {
  background: #1d4ed8;
}

.password-message {
  margin: 0;
  color: #64748b;
  font-size: 13px;
}

.security-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;

  margin-top: 24px;
  padding: 17px 20px;

  border: 1px solid #dbeafe;
  border-radius: 14px;

  background: #f8fbff;
  color: #2563eb;
}

.security-info strong {
  display: block;
  margin-bottom: 4px;
  color: #172033;
  font-size: 14px;
}

.security-info p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
  line-height: 1.5;
}

.logout-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;

  margin-top: 20px;
  padding: 10px 14px;

  border: 1px solid #e0e6ee;
  border-radius: 9px;

  background: #ffffff;
  color: #475569;

  cursor: pointer;

  font-size: 14px;
  font-weight: 600;
}

.logout-button:hover {
  border-color: #fecaca;
  background: #fff7f7;
  color: #dc2626;
}

@media (max-width: 850px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .security-item {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>