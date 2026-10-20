<template>
  <aside class="sidebar">
    <div>
      <div class="brand">
        <div class="brand-circle"></div>
        <h1>MyMeds</h1>
      </div>

      <p class="portal-label">
        Admin Portal
      </p>

      <div class="sidebar-divider"></div>

      <p class="menu-label">
        MENU
      </p>

      <nav class="sidebar-nav">
        <RouterLink
          to="/admin"
          class="nav-item"
        >
          <LayoutDashboard :size="21" />
          <span>Dashboard</span>
        </RouterLink>

        <RouterLink
          to="/admin/users"
          class="nav-item"
        >
          <Users :size="21" />
          <span>Users</span>
        </RouterLink>

        <RouterLink
          to="/admin/doctors"
          class="nav-item"
        >
          <Stethoscope :size="21" />
          <span>Doctors</span>
        </RouterLink>

        <RouterLink
          to="/admin/patients"
          class="nav-item"
        >
          <UserRound :size="21" />
          <span>Patients</span>
        </RouterLink>

        <RouterLink
          to="/admin/security"
          class="nav-item"
        >
          <ShieldCheck :size="21" />
          <span>Security Logs</span>
        </RouterLink>
      </nav>
    </div>

    <button
      class="logout-button"
      type="button"
    >
      <LogOut :size="21" />
      <span>Logout</span>
    </button>
  </aside>
</template>

<script setup>
import {
  LayoutDashboard,
  Users,
  Stethoscope,
  UserRound,
  ShieldCheck,
  LogOut,
} from '@lucide/vue'
</script>

<style scoped>
.sidebar {
  position: fixed;
  top: 0;
  left: 0;

  width: 280px;
  height: 100vh;

  display: flex;
  flex-direction: column;
  justify-content: space-between;

  padding: 30px 22px;

  background: #ffffff;
  border-right: 1px solid #e2e8f0;

  z-index: 100;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;

  padding: 0 8px;
}

.brand-circle {
  width: 12px;
  height: 12px;

  border-radius: 50%;

  background: #2563eb;
}

.brand h1 {
  margin: 0;

  color: #172033;

  font-size: 22px;
  font-weight: 700;
}

.portal-label {
  margin: 8px 8px 22px;

  color: #64748b;

  font-size: 14px;
  font-weight: 500;
}

.sidebar-divider {
  height: 1px;

  margin: 0 8px 24px;

  background: #e2e8f0;
}

.menu-label {
  margin: 0 8px 12px;

  color: #94a3b8;

  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 13px;

  width: 100%;

  padding: 12px 13px;

  border-radius: 8px;

  color: #64748b;

  text-decoration: none;

  font-size: 14px;
  font-weight: 500;

  transition:
    background 0.15s ease,
    color 0.15s ease;
}

.nav-item:hover {
  background: #f1f5f9;
  color: #172033;
}

.nav-item.router-link-active {
  background: #eff6ff;
  color: #2563eb;

  font-weight: 600;
}

.nav-item :deep(svg) {
  flex-shrink: 0;
}

.logout-button {
  display: flex;
  align-items: center;
  gap: 13px;

  width: 100%;

  padding: 12px 13px;

  border: none;
  border-radius: 8px;

  background: transparent;

  color: #64748b;

  font-family: inherit;
  font-size: 14px;
  font-weight: 500;

  text-align: left;

  cursor: pointer;
}

.logout-button:hover {
  background: #f1f5f9;
  color: #dc2626;
}

.logout-button :deep(svg) {
  flex-shrink: 0;
}

@media (max-width: 700px) {
  .sidebar {
    position: relative;

    width: 100%;
    height: auto;

    border-right: none;
    border-bottom: 1px solid #e2e8f0;
  }
}
</style>
