const API_BASE = '/api'

let authHeader = null

export async function login(username, password) {
  const encodedCredentials = btoa(`${username}:${password}`)

  const response = await fetch(`${API_BASE}/home`, {
    headers: {
      Authorization: `Basic ${encodedCredentials}`,
    },
  })

  if (!response.ok) {
    throw new Error('Login failed')
  }

  authHeader = `Basic ${encodedCredentials}`

  return response.text()
}

async function request(url, options = {}) {
  const response = await fetch(`${API_BASE}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(authHeader && {
        Authorization: authHeader,
      }),
      ...options.headers,
    },
  })

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`)
  }

  if (response.status === 204) {
    return null
  }

  const contentType = response.headers.get('content-type')

  if (contentType?.includes('application/json')) {
    return response.json()
  }

  return response.text()
}

export function getCurrentDoctor() {
  return request('/doctor/me')
}

export function getDoctorDashboard() {
  return request('/doctor/dashboard')
}

export function getDoctorPatients() {
  return request('/doctor/patients')
}

export function getPatient(patientId) {
  return request(`/doctor/patients/${patientId}`)
}

export function getMedication(patientId, medicationId) {
  return request(
    `/doctor/patients/${patientId}/medications/${medicationId}`
  )
}

export function addMedication(medication) {
  return request('/medications', {
    method: 'POST',
    body: JSON.stringify(medication),
  })
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

export function getMedicationHistory() {
  return request('/doctor/medication-history')
}
