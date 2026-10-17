<template>
  <aside class="sidebar">
    <div>
      <div class="brand">
        <div class="brand-circle"></div>
        <h1>MyMeds</h1>
      </div>

      <p class="portal-label">Patient Portal</p>

      <div class="sidebar-divider"></div>

      <p class="menu-label">MENU</p>

      <nav class="sidebar-nav">
        <RouterLink to="/patient" class="nav-item">
          <LayoutDashboard :size="21" />
          <span>Dashboard</span>
        </RouterLink>

        <RouterLink to="/patient/medications" class="nav-item">
          <Pill :size="21" />
          <span>My Medications</span>
        </RouterLink>

        <RouterLink to="/patient/history" class="nav-item">
          <History :size="21" />
          <span>Medication History</span>
        </RouterLink>

        <RouterLink to="/patient/profile" class="nav-item">
          <UserCircle :size="21" />
          <span>My Profile</span>
        </RouterLink>
      </nav>
    </div>

    <button class="logout-button" type="button">
      <LogOut :size="21" />
      <span>Logout</span>
    </button>
  </aside>
</template>

<script setup>
import {
  LayoutDashboard,
  Pill,
  History,
  UserCircle,
  LogOut,
} from '@lucide/vue'
</script>