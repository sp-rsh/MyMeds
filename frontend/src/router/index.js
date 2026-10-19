import { createRouter, createWebHistory } from 'vue-router'

import DoctorDashboard from '../views/DoctorDashboard.vue'
import DoctorPatients from '../views/DoctorPatients.vue'
import PatientDetails from '../views/PatientDetails.vue'
import ManageMedication from '../views/ManageMedication.vue'
import MedicationHistory from '../views/MedicationHistory.vue'
import DoctorProfile from '../views/DoctorProfile.vue'

const router = createRouter({
  history: createWebHistory(),

  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: DoctorDashboard,
    },
    {
      path: '/patients',
      name: 'patients',
      component: DoctorPatients,
    },
    {
      path: '/patients/:patientId',
      name: 'patient-details',
      component: PatientDetails,
    },
    {
      path: '/patients/:patientId/medications/:medicationId',
      name: 'manage-medication',
      component: ManageMedication,
    },
    {
      path: '/history',
      name: 'history',
      component: MedicationHistory,
    },
    {
      path: '/profile',
      name: 'profile',
      component: DoctorProfile,
    },
  ],
})

export default router
