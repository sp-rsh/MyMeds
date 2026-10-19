<template>
  <section>
    <RouterLink
      :to="`/patients/${route.params.patientId}`"
      class="back-link"
    >
      ← Back to Patient Details
    </RouterLink>

    <h1 class="page-title">
      Manage Medication
    </h1>

    <p class="page-subtitle">
      Update the medication schedule for {{ patientName }}.
    </p>

    <div class="medication-form-card">
      <h2>
        Medication Schedule
      </h2>

      <form @submit.prevent="saveMedication">
        <div class="form-content">
          <div class="form-group">
            <label>
              Medication Name
            </label>

            <input
              v-model="medication.name"
              type="text"
              required
            />
          </div>

          <div class="form-group">
            <label>
              Dosage
            </label>

            <input
              v-model="medication.dosage"
              type="text"
              required
            />
          </div>

          <div class="form-group">
            <label>
              Scheduled Time
            </label>

            <input
              v-model="medication.time"
              type="time"
              required
            />
          </div>

          <div class="form-group">
            <label>
              Frequency
            </label>

            <select
              v-model="medication.frequency"
              required
            >
              <option
                disabled
                value=""
              >
                Select frequency
              </option>

              <option value="Once daily">
                Once daily
              </option>

              <option value="Twice daily">
                Twice daily
              </option>

              <option value="Three times daily">
                Three times daily
              </option>
            </select>
          </div>
        </div>

        <div class="form-footer">
          <div class="form-actions">
            <button
              type="submit"
              class="primary-button"
              :disabled="saving"
            >
              {{ saving ? 'Saving...' : 'Save Changes' }}
            </button>

            <RouterLink
              :to="`/patients/${route.params.patientId}`"
              class="secondary-button"
            >
              Cancel
            </RouterLink>
          </div>
        </div>
      </form>
    </div>

    <div
      v-if="saved"
      class="success-message"
    >
      Medication changes saved.
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

import {
  getMedication,
  updateMedication,
} from '../services/api'

const route = useRoute()

const patientName = ref('Patient A')
const saving = ref(false)
const saved = ref(false)

const medication = reactive({
  name: '',
  dosage: '',
  time: '',
  frequency: '',
})

const demoMedication = {
  patientName: 'Patient A',
  name: 'Medication 1',
  dosage: '1 tablet',
  time: '13:00',
  frequency: 'Once daily',
}

function loadMedicationData(data) {
  patientName.value = data.patientName ?? 'Patient A'
  medication.name = data.name ?? ''
  medication.dosage = data.dosage ?? ''
  medication.time = data.time ?? ''
  medication.frequency = data.frequency ?? ''
}

onMounted(async () => {
  try {
    const data = await getMedication(
      route.params.patientId,
      route.params.medicationId
    )

    loadMedicationData(data)
  } catch {
    // Temporary fallback until backend integration is complete
    loadMedicationData(demoMedication)
  }
})

async function saveMedication() {
  saving.value = true
  saved.value = false

  try {
    await updateMedication(
      route.params.patientId,
      route.params.medicationId,
      {
        name: medication.name,
        dosage: medication.dosage,
        time: medication.time,
        frequency: medication.frequency,
      }
    )

    saved.value = true
  } catch {
    alert('Medication could not be updated.')
  } finally {
    saving.value = false
  }
}
</script>
