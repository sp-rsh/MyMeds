<script setup>
import { useRouter } from 'vue-router'

import {
  LayoutDashboard,
  Users,
  History,
  UserCircle,
  LogOut,
} from 'lucide-vue-next'

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
        Doctor Portal
      </p>

      <div class="sidebar-divider"></div>

      <p class="menu-label">
        MENU
      </p>

      <nav class="sidebar-nav">
        <RouterLink
          to="/doctor"
          class="nav-item"
        >
          <LayoutDashboard :size="21" />

          <span>
            Dashboard
          </span>
        </RouterLink>

        <RouterLink
          to="/doctor/patients"
          class="nav-item"
        >
          <Users :size="21" />

          <span>
            Patients
          </span>
        </RouterLink>

        <RouterLink
          to="/doctor/history"
          class="nav-item"
        >
          <History :size="21" />

          <span>
            Medication History
          </span>
        </RouterLink>

        <RouterLink
          to="/doctor/profile"
          class="nav-item"
        >
          <UserCircle :size="21" />

          <span>
            My Profile
          </span>
        </RouterLink>
      </nav>
    </div>

    <button
      class="logout-button"
      type="button"
      @click="handleLogout"
    >
      <LogOut :size="21" />

      <span>
        Log out
      </span>
    </button>
  </aside>
</template>
