<template>
  <section>
    <h1 class="page-title">
      Good morning, {{ dashboard.doctorName }}
    </h1>

    <p class="page-subtitle">
      Here's an overview of your patients and medication activity.
    </p>

    <h2 class="section-title">
      Patient Overview
    </h2>

    <div class="summary-grid">
      <div class="summary-card">
        <p class="card-label">
          Total Patients
        </p>

        <h3>
          {{ dashboard.totalPatients }}
        </h3>

        <p>
          Patients currently assigned to your care
        </p>
      </div>

      <div class="summary-card">
        <p class="card-label">
          Doses Today
        </p>

        <h3>
          {{ dashboard.dosesToday }}
        </h3>

        <p>
          Scheduled medication events across your roster
        </p>
      </div>

      <div class="summary-card">
        <p class="card-label">
          Attention Needed
        </p>

        <h3>
          {{ dashboard.attentionNeeded }}
        </h3>

        <p>
          Patients requiring review or follow-up
        </p>
      </div>
    </div>

    <h2 class="section-title">
      Patient Activity
    </h2>

    <div class="table-card">
      <table>
        <thead>
          <tr>
            <th>Patient</th>
            <th>Medication</th>
            <th>Time</th>
            <th>Status</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="event in dashboard.activity"
            :key="event.id"
          >
            <td class="patient-name">
              {{ event.patient }}
            </td>

            <td>
              {{ event.medication }}
            </td>

            <td class="bold">
              {{ event.time }}
            </td>

            <td>
              <span
                class="status-badge"
                :class="statusClass(event.status)"
              >
                <span class="status-dot"></span>
                {{ event.status }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getDoctorDashboard } from '../services/api'

// demo data
const demoDashboard = {
  doctorName: 'Anvi',
  totalPatients: 2,
  dosesToday: 2,
  attentionNeeded: 1,

  activity: [
    {
      id: 1,
      patient: 'Patient A',
      medication: 'Medication 1',
      time: '13:00',
      status: 'Scheduled',
    },
    {
      id: 2,
      patient: 'Patient B',
      medication: 'Medication 2',
      time: '12:00',
      status: 'Taken',
    },
  ],
}

const dashboard = ref(demoDashboard)

function statusClass(status) {
  return status?.toLowerCase() ?? ''
}

onMounted(async () => {
  try {
    const data = await getDoctorDashboard()

    if (data) {
      dashboard.value = data
    }
  } catch {
    console.log('using demo data')
  }
})
</script>
