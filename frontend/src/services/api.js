const API_BASE = '/api'

async function request(url, options = {}) {
  const response = await fetch(`${API_BASE}${url}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}


// Doctor

export function getCurrentDoctor() {
  return request('/doctor/me')
}

export function getDoctorDashboard() {
  return request('/doctor/dashboard')
}


// Patients

export function getDoctorPatients() {
  return request('/doctor/patients')
}

export function getPatient(patientId) {
  return request(`/doctor/patients/${patientId}`)
}


// Medications

export function getMedication(patientId, medicationId) {
  return request(
    `/doctor/patients/${patientId}/medications/${medicationId}`
  )
}

export function updateMedication(
  patientId,
  medicationId,
  medication
) {
  return request(
    `/doctor/patients/${patientId}/medications/${medicationId}`,
    {
      method: 'PUT',
      body: JSON.stringify(medication),
    }
  )
}

export function removeMedication(patientId, medicationId) {
  return request(
    `/doctor/patients/${patientId}/medications/${medicationId}`,
    {
      method: 'DELETE',
    }
  )
}


// History

export function getMedicationHistory() {
  return request('/doctor/medication-history')
}
