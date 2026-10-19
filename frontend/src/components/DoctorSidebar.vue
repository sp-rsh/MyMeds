<template>
  <aside class="sidebar">
    <div>
      <div class="brand">
        <div class="brand-circle"></div>
        <h1>MyMeds</h1>
      </div>

      <p class="portal-label">Doctor Portal</p>

      <div class="sidebar-divider"></div>

      <p class="menu-label">MENU</p>

      <nav class="sidebar-nav">
        <RouterLink to="/" class="nav-item">
          <LayoutDashboard :size="21" />
          Dashboard
        </RouterLink>

        <RouterLink to="/patients" class="nav-item">
          <Users :size="21" />
          Patients
        </RouterLink>

        <RouterLink to="/history" class="nav-item">
          <History :size="21" />
          Medication History
        </RouterLink>

        <RouterLink to="/profile" class="nav-item">
          <UserCircle :size="21" />
          My Profile
        </RouterLink>
      </nav>
    </div>

    <button class="logout-button">
      <LogOut :size="21" />
      Logout
    </button>
  </aside>
</template>

<script setup>
import {
  LayoutDashboard,
  Users,
  History,
  UserCircle,
  LogOut,
} from 'lucide-vue-next'
</script>
