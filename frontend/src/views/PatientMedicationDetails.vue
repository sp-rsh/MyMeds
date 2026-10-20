<script setup>
import {
  computed,
  onMounted,
  ref,
} from 'vue'

import {
  useRoute,
  RouterLink,
} from 'vue-router'

import {
  Pill,
  ArrowLeft,
  Clock3,
  CalendarDays,
  CheckCircle2,
  Info,
  CircleAlert,
  LoaderCircle,
} from '@lucide/vue'

import {
  getPatientMedications,
} from '../services/api'


const route = useRoute()

const medications = ref([])

const loading = ref(true)
const errorMessage = ref('')


async function loadMedication() {
  loading.value = true
  errorMessage.value = ''

  try {
    const result =
      await getPatientMedications()

    medications.value =
      result.map(addDisplayStatus)

  } catch (error) {
    console.error(
      'Failed to load medication:',
      error
    )

    medications.value = []

    errorMessage.value =
      getErrorMessage(error)

  } finally {
    loading.value = false
  }
}


const medication = computed(() => {
  const routeId =
    decodeURIComponent(
      String(route.params.id || '')
    )

  return medications.value.find(
    item =>
      String(item.id) === routeId
  ) ?? null
})


function addDisplayStatus(medication) {
  const raw =
    String(
      medication.status || ''
    ).toLowerCase()

  let status

  if (
    raw === 'taken' ||
    raw === 'completed' ||
    raw === 'collected'
  ) {
    status = 'Taken'

  } else if (raw === 'scheduled') {
    status = 'Scheduled'

  } else if (raw === 'upcoming') {
    status = 'Upcoming'

  } else {
    status = 'Scheduled'
  }

  return {
    ...medication,

    status,

    statusClass:
      status.toLowerCase(),
  }
}


function getErrorMessage(error) {
  if (error?.status === 401) {
    return 'Authentication is required.'
  }

  if (error?.status === 403) {
    return 'You do not have permission to view this medication.'
  }

  return 'Medication information is currently unavailable.'
}


onMounted(loadMedication)
</script>


<template>
  <section class="details-page">

    <div class="back-row">

      <RouterLink
        to="/patient/medications"
        class="back-button"
      >
        <ArrowLeft :size="18" />

        Back to My Medications
      </RouterLink>

    </div>


    <header class="page-header">

      <h1 class="page-title">
        Medication Details
      </h1>

      <p class="page-subtitle">
        View the details of your prescribed medication.
      </p>

    </header>


    <div
      v-if="errorMessage"
      class="error-box"
    >
      <CircleAlert :size="20" />

      <span>
        {{ errorMessage }}
      </span>
    </div>


    <div
      v-if="loading"
      class="loading-state"
    >
      <LoaderCircle
        :size="30"
        class="loading-icon"
      />

      Loading medication...
    </div>


    <article
      v-else-if="medication"
      class="details-card"
    >

      <div class="medication-header">

        <div class="large-medication-icon">
          <Pill :size="28" />
        </div>


        <div>

          <p class="eyebrow">
            MEDICATION
          </p>

          <h2>
            {{ medication.name }}
          </h2>

          <p class="dosage">
            {{ medication.dosage || 'No dosage specified' }}
          </p>

        </div>


        <span
          class="status-badge"
          :class="medication.statusClass"
        >

          <CheckCircle2
            v-if="medication.status === 'Taken'"
            :size="16"
          />

          <Clock3
            v-else
            :size="16"
          />

          {{ medication.status }}

        </span>

      </div>


      <div class="divider"></div>


      <div class="details-grid">

        <div class="detail-item">

          <div class="detail-icon">
            <Clock3 :size="20" />
          </div>

          <div>
            <span class="detail-label">
              Scheduled Time
            </span>

            <strong>
              {{ medication.time || '—' }}
            </strong>
          </div>

        </div>


        <div class="detail-item">

          <div class="detail-icon">
            <CalendarDays :size="20" />
          </div>

          <div>
            <span class="detail-label">
              Frequency
            </span>

            <strong>
              {{ medication.frequency || '—' }}
            </strong>
          </div>

        </div>


        <div class="detail-item">

          <div class="detail-icon">
            <Pill :size="20" />
          </div>

          <div>
            <span class="detail-label">
              Dosage
            </span>

            <strong>
              {{ medication.dosage || '—' }}
            </strong>
          </div>

        </div>


        <div class="detail-item">

          <div class="detail-icon">
            <CheckCircle2 :size="20" />
          </div>

          <div>
            <span class="detail-label">
              Today's Status
            </span>

            <strong>
              {{ medication.status }}
            </strong>
          </div>

        </div>

      </div>

    </article>


    <div
      v-else-if="!errorMessage"
      class="empty-state"
    >
      <Pill :size="30" />

      <strong>
        Medication not found
      </strong>

      <span>
        This medication is not available in your current prescription list.
      </span>
    </div>


    <div class="info-bar">

      <Info :size="20" />

      <p>
        Your medication schedule is prescribed and managed by your care team.
        If you believe your medication details need to be changed,
        please contact your doctor.
      </p>

    </div>

  </section>
</template>


<style scoped>
.details-page {
  max-width: 1000px;
}

.back-row {
  margin-bottom: 22px;
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  color: #64748b;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
}

.back-button:hover {
  color: #2563eb;
}

.page-header {
  margin-bottom: 28px;
}

.details-card {
  padding: 30px;
  background: #ffffff;
  border: 1px solid #e0e6ee;
  border-radius: 18px;
}

.medication-header {
  display: flex;
  align-items: center;
  gap: 17px;
}

.large-medication-icon {
  width: 58px;
  height: 58px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 15px;
  background: #eaf2ff;
  color: #2563eb;
}

.eyebrow {
  margin: 0 0 4px;
  color: #94a3b8;
  font-size: 12px;
  font-weight: 700;
}

.medication-header h2 {
  margin: 0;
  color: #172033;
  font-size: 25px;
}

.dosage {
  margin: 5px 0 0;
  color: #64748b;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-left: auto;
  padding: 8px 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
}

.taken {
  background: #ecfdf3;
  color: #15803d;
}

.scheduled {
  background: #eaf2ff;
  color: #2563eb;
}

.upcoming {
  background: #f1f5f9;
  color: #64748b;
}

.divider {
  height: 1px;
  margin: 30px 0;
  background: #e0e6ee;
}

.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 13px;
  padding: 18px;
  border: 1px solid #e5eaf1;
  border-radius: 13px;
  background: #fafbfc;
}

.detail-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: #ffffff;
  color: #2563eb;
  border: 1px solid #e0e6ee;
}

.detail-item div:last-child {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  color: #94a3b8;
  font-size: 12px;
}

.info-bar {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  padding: 17px 20px;
  border: 1px solid #dbeafe;
  border-radius: 14px;
  background: #f8fbff;
  color: #2563eb;
}

.info-bar p {
  margin: 0;
  color: #64748b;
  line-height: 1.5;
}

.error-box,
.loading-state,
.empty-state {
  padding: 40px 20px;
  text-align: center;
}

.error-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 9px;
  border: 1px solid #fecaca;
  border-radius: 12px;
  background: #fef2f2;
  color: #b91c1c;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #94a3b8;
}

.empty-state strong {
  color: #475569;
}

.loading-icon {
  animation: spin .9s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 700px) {
  .details-grid {
    grid-template-columns: 1fr;
  }

  .medication-header {
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .status-badge {
    margin-left: 0;
  }
}
</style>
