<script setup>
import PatientSidebar from './components/PatientSidebar.vue'
</script>

<template>
  <div class="app-layout">
    <PatientSidebar />

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style>
* {
  box-sizing: border-box;
}

html,
body,
#app {
  margin: 0;
  min-height: 100%;
}

body {
  background: #f7f9fc;
  color: #172033;
  font-family:
    Inter,
    -apple-system,
    BlinkMacSystemFont,
    "Segoe UI",
    sans-serif;
}

button,
input,
select,
textarea {
  font: inherit;
}

.app-layout {
  min-height: 100vh;
}

.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 280px;
  height: 100vh;

  display: flex;
  flex-direction: column;
  justify-content: space-between;

  padding: 30px 20px;

  background: #ffffff;
  border-right: 1px solid #e0e6ee;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 10px;
}

.brand-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #2563eb;
}

.brand h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.portal-label {
  margin: 8px 10px 22px;
  color: #64748b;
  font-size: 14px;
  font-weight: 600;
}

.sidebar-divider {
  height: 1px;
  margin: 0 10px 24px;
  background: #e0e6ee;
}

.menu-label {
  margin: 0 10px 10px;
  color: #94a3b8;
  font-size: 12px;
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

  padding: 13px 14px;

  border-radius: 14px;

  color: #475569;
  text-decoration: none;

  font-size: 16px;
  font-weight: 500;

  transition:
    background 0.15s ease,
    color 0.15s ease;
}

.nav-item:hover {
  background: #f1f5f9;
  color: #2563eb;
}

.nav-item.router-link-active {
  background: #eaf2ff;
  color: #2563eb;
  font-weight: 600;
}

.logout-button {
  display: flex;
  align-items: center;
  gap: 13px;

  width: 100%;
  padding: 13px 14px;

  border: 0;
  border-radius: 14px;

  background: transparent;
  color: #475569;

  cursor: pointer;
  font-size: 16px;
  font-weight: 500;

  text-align: left;
}

.logout-button:hover {
  background: #f1f5f9;
  color: #2563eb;
}

.main-content {
  width: calc(100% - 280px);
  min-height: 100vh;

  margin-left: 280px;
  padding: 48px 42px;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  margin: 0;
  color: #172033;
  font-size: 40px;
  font-weight: 700;
  letter-spacing: -1px;
}

.page-subtitle {
  margin: 10px 0 0;
  color: #64748b;
  font-size: 18px;
  line-height: 1.5;
}

.dashboard-placeholder {
  padding: 32px;

  background: #ffffff;
  border: 1px solid #e0e6ee;
  border-radius: 18px;
}

.dashboard-placeholder h2 {
  margin: 0 0 8px;
  font-size: 24px;
}

.dashboard-placeholder p {
  margin: 0;
  color: #64748b;
}
</style>