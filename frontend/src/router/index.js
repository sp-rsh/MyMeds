import {
  createRouter,
  createWebHistory,
} from 'vue-router'

import LoginView from '../views/LoginView.vue'

import DoctorDashboard from '../views/DoctorDashboard.vue'
import DoctorPatients from '../views/DoctorPatients.vue'
import PatientDetails from '../views/PatientDetails.vue'
import ManageMedication from '../views/ManageMedication.vue'
import MedicationHistory from '../views/MedicationHistory.vue'
import DoctorProfile from '../views/DoctorProfile.vue'

const router = createRouter({
  history: createWebHistory(
    import.meta.env.BASE_URL
  ),

  routes: [
    {
      path: '/',
      redirect: '/login',
    },

    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },

    {
      path: '/doctor',
      name: 'doctor-dashboard',
      component: DoctorDashboard,
      meta: {
        role: 'DOCTOR',
      },
    },

    {
      path: '/doctor/patients',
      name: 'doctor-patients',
      component: DoctorPatients,
      meta: {
        role: 'DOCTOR',
      },
    },

    {
      path: '/doctor/patients/:patientId',
      name: 'patient-details',
      component: PatientDetails,
      meta: {
        role: 'DOCTOR',
      },
    },

    {
      path:
        '/doctor/patients/:patientId/medications/:medicationId',
      name: 'manage-medication',
      component: ManageMedication,
      meta: {
        role: 'DOCTOR',
      },
    },

    {
      path: '/doctor/history',
      name: 'doctor-history',
      component: MedicationHistory,
      meta: {
        role: 'DOCTOR',
      },
    },

    {
      path: '/doctor/profile',
      name: 'doctor-profile',
      component: DoctorProfile,
      meta: {
        role: 'DOCTOR',
      },
    },

    {
      path: '/patient',
      name: 'patient-dashboard',
      component: () =>
        import(
          '../views/PatientDashboard.vue'
        ),
      meta: {
        role: 'PATIENT',
      },
    },

    {
      path: '/patient/medications',
      name: 'patient-medications',
      component: () =>
        import(
          '../views/PatientMedications.vue'
        ),
      meta: {
        role: 'PATIENT',
      },
    },

    {
      path: '/patient/history',
      name: 'patient-history',
      component: () =>
        import(
          '../views/PatientHistory.vue'
        ),
      meta: {
        role: 'PATIENT',
      },
    },

    {
      path: '/patient/profile',
      name: 'patient-profile',
      component: () =>
        import(
          '../views/PatientProfile.vue'
        ),
      meta: {
        role: 'PATIENT',
      },
    },

    {
      path: '/admin',
      redirect: '/admin/users',
    },

    {
      path: '/admin/users',
      name: 'admin-users',
      component: () =>
        import(
          '../views/AdminUsers.vue'
        ),
      meta: {
        role: 'ADMIN',
      },
    },

    {
      path: '/admin/doctors',
      name: 'admin-doctors',
      component: () =>
        import(
          '../views/AdminDoctors.vue'
        ),
      meta: {
        role: 'ADMIN',
      },
    },

    {
      path: '/admin/patients',
      name: 'admin-patients',
      component: () =>
        import(
          '../views/AdminPatients.vue'
        ),
      meta: {
        role: 'ADMIN',
      },
    },

    {
      path: '/admin/security',
      name: 'admin-security',
      component: () =>
        import(
          '../views/AdminSecurityLogs.vue'
        ),
      meta: {
        role: 'ADMIN',
      },
    },

    {
      path: '/unauthorized',
      name: 'unauthorized',
      component: () =>
        import(
          '../views/Unauthorized.vue'
        ),
    },
  ],
})

export default router
