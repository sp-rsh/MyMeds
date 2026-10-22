<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

import DoctorSidebar from './components/DoctorSidebar.vue'
import PatientSidebar from './components/PatientSidebar.vue'
import AdminSidebar from './components/AdminSidebar.vue'

const route = useRoute()

const isLoginPage = computed(() => route.path === '/login')
const isUnauthorizedPage = computed(() => route.path === '/unauthorized')

const isAdmin = computed(() => route.path.startsWith('/admin'))
const isPatient = computed(() => route.path.startsWith('/patient'))

const isDoctor = computed(
  () =>
    !isLoginPage.value &&
    !isUnauthorizedPage.value &&
    !isAdmin.value &&
    !isPatient.value,
)
</script>

<template>
  <RouterView v-if="isLoginPage || isUnauthorizedPage" />

  <div v-else class="app-layout">
    <DoctorSidebar v-if="isDoctor" />
    <PatientSidebar v-else-if="isPatient" />
    <AdminSidebar v-else-if="isAdmin" />

    <main
      class="main-content"
      :class="{
        'doctor-content': isDoctor,
        'patient-content': isPatient,
        'admin-content': isAdmin,
      }"
    >
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
  width: 100%;
}

body {
  background: #f8fafc;
  color: #172033;
  font-family:
    Inter,
    -apple-system,
    BlinkMacSystemFont,
    'Segoe UI',
    sans-serif;
}

.app-layout {
  min-height: 100vh;
  width: 100%;
}

.main-content {
  min-height: 100vh;
  margin-left: 280px;
  padding: 48px 42px;
}

.doctor-content,
.patient-content,
.admin-content {
  margin-left: 280px;
}
</style>