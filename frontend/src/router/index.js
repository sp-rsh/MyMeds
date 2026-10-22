import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'

import DoctorDashboard from '../views/DoctorDashboard.vue'
import DoctorPatients from '../views/DoctorPatients.vue'
import PatientDetails from '../views/PatientDetails.vue'
import ManageMedication from '../views/ManageMedication.vue'
import MedicationHistory from '../views/MedicationHistory.vue'
import DoctorProfile from '../views/DoctorProfile.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),

  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/doctor',
      name: 'doctor-dashboard',
      component: DoctorDashboard,
    },
    {
      path: '/doctor/patients',
      name: 'doctor-patients',
      component: DoctorPatients,
    },
    {
      path: '/doctor/patients/:patientId',
      name: 'patient-details',
      component: PatientDetails,
    },
    {
      path: '/doctor/patients/:patientId/medications/:medicationId',
      name: 'manage-medication',
      component: ManageMedication,
    },
    {
      path: '/doctor/history',
      name: 'doctor-history',
      component: MedicationHistory,
    },
    {
      path: '/doctor/profile',
      name: 'doctor-profile',
      component: DoctorProfile,
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
    {
      path: '/admin',
      name: 'admin-dashboard',
      component: () => import('../views/AdminDashboard.vue'),
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('../views/AdminUsers.vue'),
    },
    {
      path: '/admin/doctors',
      name: 'admin-doctors',
      component: () => import('../views/AdminDoctors.vue'),
    },
    {
      path: '/admin/patients',
      name: 'admin-patients',
      component: () => import('../views/AdminPatients.vue'),
    },
    {
      path: '/admin/security',
      name: 'admin-security',
      component: () => import('../views/AdminSecurityLogs.vue'),
    },
    {
      path: '/admin/settings',
      name: 'admin-settings',
      component: () => import('../views/AdminSettings.vue'),
    },
    {
      path: '/unauthorized',
      name: 'unauthorized',
      component: () => import('../views/Unauthorized.vue'),
    },
  ],
})

export default router