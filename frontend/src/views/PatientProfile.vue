<script setup>
import {
  UserCircle,
  Stethoscope,
  ShieldCheck,
} from '@lucide/vue'
</script>

<template>
  <section class="profile-page">
    <header class="page-header">
      <h1 class="page-title">
        My Profile
      </h1>

      <p class="page-subtitle">
        View your account and care information.
      </p>
    </header>

    <div class="profile-layout">
      <article class="profile-card">
        <div class="card-header">
          <div class="profile-avatar">
            <UserCircle :size="34" />
          </div>

          <div>
            <h2>
              Anvi Chahal
            </h2>

            <p>
              Patient
            </p>
          </div>
        </div>

        <div class="divider"></div>

        <div class="profile-details">
          <div class="detail-row">
            <div class="detail-icon">
              <UserCircle :size="19" />
            </div>

            <div class="detail-content">
              <span class="detail-label">
                Name
              </span>

              <strong>
                Anvi Chahal
              </strong>
            </div>
          </div>

          <div class="detail-row">
            <div class="detail-icon">
              <UserCircle :size="19" />
            </div>

            <div class="detail-content">
              <span class="detail-label">
                Role
              </span>

              <strong>
                Patient
              </strong>
            </div>
          </div>

          <div class="detail-row">
            <div class="detail-icon">
              <Stethoscope :size="19" />
            </div>

            <div class="detail-content">
              <span class="detail-label">
                Assigned Doctor
              </span>

              <strong>
                Dr. Sharma
              </strong>
            </div>
          </div>
        </div>
      </article>

      <article class="security-card">
        <div class="card-title-row">
          <div class="section-icon">
            <ShieldCheck :size="21" />
          </div>

          <div>
            <h2>
              Account Security
            </h2>

            <p>
              Account authentication and access are managed securely by MyMeds.
            </p>
          </div>
        </div>

        <div class="security-text">
          <p>
            Password and authentication settings will be available once the
            account security functionality is fully connected to the backend.
          </p>
        </div>
      </article>
    </div>

    <div class="security-info">
      <ShieldCheck :size="20" />

      <div>
        <strong>
          Medication access is protected
        </strong>

        <p>
          Medication information can be viewed by the patient and authorized
          care team members according to their assigned roles.
        </p>
      </div>
    </div>
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
  gap: 18px;
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

  border-radius: 10px;

  background: #f8fafc;
  color: #2563eb;

  border: 1px solid #e2e8f0;
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

.detail-content strong {
  color: #172033;

  font-size: 15px;
  font-weight: 600;
}

.card-title-row {
  display: flex;
  align-items: center;
  gap: 13px;
}

.section-icon {
  width: 44px;
  height: 44px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 12px;

  background: #eff6ff;
  color: #2563eb;
}

.card-title-row h2 {
  margin: 0;

  color: #172033;

  font-size: 20px;
  font-weight: 700;
}

.card-title-row p {
  margin: 5px 0 0;

  color: #64748b;

  font-size: 14px;
}

.security-text {
  margin-top: 26px;

  padding: 18px;

  border: 1px solid #e2e8f0;
  border-radius: 12px;

  background: #f8fafc;
}

.security-text p {
  margin: 0;

  color: #64748b;

  font-size: 14px;
  line-height: 1.6;
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

@media (max-width: 850px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
}
</style>
