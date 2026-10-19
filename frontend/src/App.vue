<template>
  <RouterView v-if="isLoginPage" />
  <RouterView v-else-if="isUnauthorizedPage" />

  <div v-else class="app-layout">
    <DoctorSidebar v-if="isDoctorPortal" />
    <PatientSidebar v-else-if="isPatientPortal" />
    <AdminSidebar v-else-if="isAdminPortal" />

    <main class="main-content">
      <RouterView />
    </main>

  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

import DoctorSidebar from './components/DoctorSidebar.vue'
import PatientSidebar from './components/PatientSidebar.vue'
import AdminSidebar from './components/AdminSidebar.vue'

const route = useRoute()

const isLoginPage = computed(() => {
  return route.path === '/login'
})

const isUnauthorizedPage = computed(() => {
  return route.path === '/unauthorized'
})

const isDoctorPortal = computed(() => {
  return (
    route.path === '/' ||
    route.path === '/patients' ||
    route.path.startsWith('/patients/')
  )
})

const isPatientPortal = computed(() => {
  return route.path.startsWith('/patient')
})

const isAdminPortal = computed(() => {
  return route.path.startsWith('/admin')
})
</script>

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
  margin: 0;
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

@media (max-width: 700px) {
  .main-content {
    width: 100%;
    margin-left: 0;
    padding: 30px 20px;
  }
}
</style>