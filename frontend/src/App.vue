<template>
  <RouterView v-if="isLoginPage" />

  <div
    v-else
    class="app-layout"
  >
    <DoctorSidebar />

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

import DoctorSidebar from './components/DoctorSidebar.vue'

const route = useRoute()

const isLoginPage = computed(() => {
  return route.path === '/login'
})
</script>
