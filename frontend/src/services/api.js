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
    const error = new Error(`Login failed: ${response.status}`)
    error.status = response.status
    throw error
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
    const error = new Error(
      `Request failed: ${response.status}`
    )

    error.status = response.status

    throw error
  }

  if (response.status === 204) {
    return null
  }

  const contentType =
    response.headers.get('content-type')

  if (
    contentType &&
    contentType.includes('application/json')
  ) {
    return response.json()
  }

  return response.text()
}


export function getCurrentUser() {
  return request('/users/me')
}


export async function getPatientMedications() {
  const result = await request('/medications/my')

  const medications =
    Array.isArray(result)
      ? result
      : Array.isArray(result?.content)
        ? result.content
        : Array.isArray(result?.medications)
          ? result.medications
          : []

  return medications.map((medication, index) => ({
    ...medication,

    id:
      medication.id ??
      medication.medicationId ??
      medication.name ??
      medication.medicationName ??
      `medication-${index}`,

    name:
      medication.name ??
      medication.medicationName ??
      'Medication',

    dosage:
      medication.dosage ??
      '',

    time:
      String(
        medication.time ??
        medication.scheduledTime ??
        ''
      ).slice(0, 5),

    frequency:
      medication.frequency ??
      'Once daily',

    status:
      medication.status ??
      '',
  }))
}


export function getPatientLogs() {
  return request('/logs')
}


export function getPatientAlerts() {
  return request('/alerts')
}


export function changePassword(
  oldPassword,
  newPassword
) {
  return request('/reset-password', {
    method: 'POST',

    body: JSON.stringify({
      oldPassword,
      newPassword,
    }),
  })
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
  return request(
    `/doctor/patients/${encodeURIComponent(patientId)}`
  )
}


export function getMedication(
  patientId,
  medicationId
) {
  return request(
    `/doctor/patients/${encodeURIComponent(patientId)}` +
    `/medications/${encodeURIComponent(medicationId)}`
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
    `/doctor/patients/${encodeURIComponent(patientId)}` +
    `/medications/${encodeURIComponent(medicationId)}`,
    {
      method: 'PUT',

      body: JSON.stringify(medication),
    }
  )
}


export function removeMedication(
  patientId,
  medicationId
) {
  return request(
    `/doctor/patients/${encodeURIComponent(patientId)}` +
    `/medications/${encodeURIComponent(medicationId)}`,
    {
      method: 'DELETE',
    }
  )
}


export function getMedicationHistory() {
  return request('/doctor/medication-history')
}



export function getAdminUsers(role) {
  const query =
    role && role !== 'ALL'
      ? `?role=${encodeURIComponent(role)}`
      : ''

  return request(`/users${query}`)
}


export function registerUser(user) {
  return request('/users/register', {
    method: 'POST',

    body: JSON.stringify(user),
  })
}


export function updateAdminUser(id, user) {
  return request(
    `/users/${encodeURIComponent(id)}`,
    {
      method: 'PUT',

      body: JSON.stringify(user),
    }
  )
}


export function updateAdminUserRole(id, role) {
  return request(
    `/users/${encodeURIComponent(id)}/role`,
    {
      method: 'PATCH',

      body: JSON.stringify({
        role,
      }),
    }
  )
}


export function updateAdminUserStatus(
  id,
  active
) {
  return request(
    `/users/${encodeURIComponent(id)}/status`,
    {
      method: 'PATCH',

      body: JSON.stringify({
        active,
      }),
    }
  )
}



export function getDoctorAssignedPatients(
  doctorId
) {
  return request(
    `/users/${encodeURIComponent(doctorId)}/patients`
  )
}


export function updateDoctorPatientAssignments(
  doctorId,
  patientIds
) {
  return request(
    `/users/${encodeURIComponent(doctorId)}/patients`,
    {
      method: 'PUT',

      body: JSON.stringify({
        patientIds,
      }),
    }
  )
}
