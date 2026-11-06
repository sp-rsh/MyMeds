<script setup>
import { useRouter } from 'vue-router'

import {
  Users,
  Stethoscope,
  UserRound,
  ShieldCheck,
  LogOut,
} from '@lucide/vue'

import { logout } from '../services/api'

const router = useRouter()

function handleLogout() {
  logout()
  router.push('/login')
}
</script>

<template>
  <aside class="sidebar">
    <div>
      <div class="brand">
        <div class="brand-circle"></div>

        <h1>
          MyMeds
        </h1>
      </div>

      <p class="portal-label">
        Admin Portal
      </p>

      <div class="sidebar-divider"></div>

      <p class="menu-label">
        MANAGEMENT
      </p>

      <nav class="sidebar-nav">
        <RouterLink
          to="/admin/users"
          class="nav-item"
        >
          <Users
            :size="22"
            class="nav-icon"
          />

          Users
        </RouterLink>

        <RouterLink
          to="/admin/doctors"
          class="nav-item"
        >
          <Stethoscope
            :size="22"
            class="nav-icon"
          />

          Doctors
        </RouterLink>

        <RouterLink
          to="/admin/patients"
          class="nav-item"
        >
          <UserRound
            :size="22"
            class="nav-icon"
          />

          Patients
        </RouterLink>

        <RouterLink
          to="/admin/security"
          class="nav-item"
        >
          <ShieldCheck
            :size="22"
            class="nav-icon"
          />

          Security Logs
        </RouterLink>
      </nav>
    </div>

    <button
      type="button"
      class="logout-button"
      @click="handleLogout"
    >
      <LogOut :size="22" />

      Log out
    </button>
  </aside>
</template>
