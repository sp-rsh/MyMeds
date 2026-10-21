<script setup>
import { useRouter } from 'vue-router'

import {
  House,
  Pill,
  History,
  UserCircle,
  LogOut,
} from '@lucide/vue'

import {
  logout,
} from '../services/api'


function handleLogout() {
  logout()
  router.push('/login')
}


const router = useRouter()
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
        Patient Portal
      </p>


      <div class="sidebar-divider"></div>


      <p class="menu-label">
        MENU
      </p>


      <nav class="sidebar-nav">

        <RouterLink
          to="/patient"
          class="nav-item"
        >
          <House
            :size="22"
            class="nav-icon"
          />

          Home
        </RouterLink>


        <RouterLink
          to="/patient/medications"
          class="nav-item"
        >
          <Pill
            :size="22"
            class="nav-icon"
          />

          My Medications
        </RouterLink>


        <RouterLink
          to="/patient/history"
          class="nav-item"
        >
          <History
            :size="22"
            class="nav-icon"
          />

          History
        </RouterLink>


        <RouterLink
          to="/patient/profile"
          class="nav-item"
        >
          <UserCircle
            :size="22"
            class="nav-icon"
          />

          My Profile
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
