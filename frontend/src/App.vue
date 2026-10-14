<template>
  <div class="app-layout">
    <DoctorSidebar />

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import DoctorSidebar from './components/DoctorSidebar.vue'
</script>
