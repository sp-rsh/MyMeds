<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

import DoctorSidebar from './components/DoctorSidebar.vue'
import PatientSidebar from './components/PatientSidebar.vue'
import AdminSidebar from './components/AdminSidebar.vue'

const route = useRoute()

const isLoginPage = computed(() => route.path === '/login')
const isUnauthorizedPage = computed(() => route.path === '/unauthorized')

const isDoctor = computed(() =>
  route.path.startsWith('/doctor')
)

const isPatient = computed(() =>
  route.path.startsWith('/patient')
)

const isAdmin = computed(() =>
  route.path.startsWith('/admin')
)

const isStandalonePage = computed(() =>
  isLoginPage.value ||
  isUnauthorizedPage.value
)
</script>

<template>
  <RouterView v-if="isStandalonePage" />

  <div
    v-else
    class="app-layout"
  >
    <DoctorSidebar v-if="isDoctor" />

    <PatientSidebar
      v-else-if="isPatient"
    />

    <AdminSidebar
      v-else-if="isAdmin"
    />

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>
