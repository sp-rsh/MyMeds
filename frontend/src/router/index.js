import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [
    {
      path: '/',
      redirect: '/patient',
    },
    {
      path: '/patient',
      name: 'patient-dashboard',
      component: () => import('../views/PatientDashboard.vue'),
    },
    {
      path: '/patient/medications',
      name: 'patient-medications',
      component: () => import('../views/PatientMedications.vue'),
    },
    {
      path: '/patient/history',
      name: 'patient-history',
      component: () => import('../views/PatientHistory.vue'),
    },
    {
      path: '/patient/profile',
      name: 'patient-profile',
      component: () => import('../views/PatientProfile.vue'),
    },
    {
      path: '/patient/medications/:id',
      name: 'patient-medication-details',
      component: () => import('../views/PatientMedicationDetails.vue'),
    },
  ],
})

export default router