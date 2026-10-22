<script setup>
import { onMounted, reactive, ref } from 'vue'

import { useRoute, useRouter } from 'vue-router'

import { getMedication, updateMedication } from '../services/api'

async function loadMedication() {
  loading.value = true
  error.value = ''

  try {
    const data = await getMedication(route.params.patientId, route.params.medicationId)

    patientName.value = data.patientName || ''

    medication.name = data.name || ''

    medication.dosage = data.dosage || ''

    medication.time = data.time || ''

    medication.towerToDispense = data.towerToDispense || ''
  } catch (err) {
    console.error('Could not load medication:', err)

    error.value = 'Medication information could not be loaded.'
  } finally {
    loading.value = false
  }
}

async function saveMedication() {
  saving.value = true
  saved.value = false
  error.value = ''

  try {
    await updateMedication(route.params.patientId, route.params.medicationId, {
      name: medication.name,
      dosage: medication.dosage,
      time: medication.time,
      towerToDispense: medication.towerToDispense,
    })

    saved.value = true

    await loadMedication()
  } catch (err) {
    console.error('Could not update medication:', err)

    error.value = err.message || 'Medication could not be updated.'
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.push(`/doctor/patients/${route.params.patientId}`)
}

const route = useRoute()
const router = useRouter()

const patientName = ref('')

const loading = ref(true)
const saving = ref(false)

const saved = ref(false)
const error = ref('')

const medication = reactive({
  name: '',
  dosage: '',
  time: '',
  towerToDispense: 0,
})

onMounted(loadMedication)
</script>

<template>
  <section>
    <button type="button" class="back-link" @click="goBack">← Back to Patient Details</button>

    <h1 class="page-title">Manage Medication</h1>

    <p class="page-subtitle">
      Update the medication schedule
      <template v-if="patientName"> for {{ patientName }} </template>.
    </p>

    <p v-if="loading" class="loading-message">Loading medication...</p>

    <p v-else-if="error" class="error-message">
      {{ error }}
    </p>

    <div v-else class="medication-form-card">
      <h2>Medication Schedule</h2>

      <form @submit.prevent="saveMedication">
        <div class="form-content">
          <div class="form-group">
            <label for="medication-name"> Medication Name </label>

            <input id="medication-name" v-model="medication.name" type="text" required />
          </div>

          <div class="form-group">
            <label for="medication-dosage"> Dosage </label>

            <input id="medication-dosage" v-model="medication.dosage" type="text" required />
          </div>

          <div class="form-group">
            <label for="medication-time"> Scheduled Time </label>

            <input id="medication-time" v-model="medication.time" type="time" required />
          </div>

          <div class="form-group">
            <label for="medication-tower_to_dispense">Tower to dispense</label>

            <select id="medication-tower_to_dispense" v-model="medication.towerToDispense" required>
              <option disabled value="">Select tower to dispense</option>

              <option value="1">Tower One</option>

              <option value="2">Tower Two</option>

              <option value="3">Tower Three</option>
            </select>
          </div>
        </div>

        <div class="form-footer">
          <div class="form-actions">
            <button type="submit" class="primary-button" :disabled="saving">
              {{ saving ? 'Saving...' : 'Save Changes' }}
            </button>

            <button type="button" class="secondary-button" @click="goBack">Cancel</button>
          </div>
        </div>
      </form>
    </div>

    <p v-if="saved" class="success-message">Medication changes saved.</p>
  </section>
</template>
