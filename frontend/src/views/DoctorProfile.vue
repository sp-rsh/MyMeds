<template>
  <section>
    <h1 class="page-title">
      My Profile
    </h1>

    <p class="page-subtitle">
      Doctor account information.
    </p>

    <div class="profile-header">
      <div class="profile-avatar">
        👤
      </div>

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
      <h3>
        PROFESSIONAL INFORMATION
      </h3>

      <div class="profile-row">
        <span>
          Name
        </span>

        <strong>
          {{ doctor.name }}
        </strong>
      </div>

      <div class="profile-row">
        <span>
          Role
        </span>

        <strong>
          {{ doctor.role }}
        </strong>
      </div>

      <div class="profile-row">
        <span>
          Email
        </span>

        <strong>
          {{ doctor.email }}
        </strong>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getCurrentDoctor } from '../services/api'

const demoDoctor = {
  name: 'Anvi',
  role: 'Doctor',
  email: 'doctor@mymeds.local',
}

const doctor = ref(demoDoctor)

onMounted(async () => {
  try {
    doctor.value = await getCurrentDoctor()
  } catch {
    // fallback until authentication is connected
    doctor.value = demoDoctor
  }
})
</script>
