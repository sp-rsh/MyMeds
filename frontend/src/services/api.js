const API_BASE = '/api'

let currentUser = null

async function parseResponse(response) {
  if (response.status === 204) {
    return null
  }

  const contentType = response.headers.get('content-type') ?? ''
  const body = await response.text()

  if (!body) {
    return null
  }

  if (contentType.includes('application/json')) {
    return JSON.parse(body)
  }

  return body
}

function getErrorMessage(body, fallback) {
  if (body && typeof body === 'object' && body.message) {
    return body.message
  }

  if (typeof body === 'string' && body.trim()) {
    return body
  }

  return fallback
}

async function request(url, options = {}) {
  const response = await fetch(`${API_BASE}${url}`, {
    ...options,
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })

  if (!response.ok) {
    const fallbackMessage = `Request failed: ${response.status}`
    let body = null

    try {
      body = await parseResponse(response)
    } catch (error) {
      if (!(error instanceof SyntaxError)) {
        throw error
      }
    }

    const requestError = new Error(getErrorMessage(body, fallbackMessage))

    requestError.status = response.status
    throw requestError
  }

  return parseResponse(response)
}

export async function login(username, password) {
  try {
    const body = await request('/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    })

    if (body.totpRequired || body.totpSetupRequired) {
      return body
    }

    return getCurrentUser()
  } catch (error) {
    if (error.status === 401) {
      error.message = 'Invalid username or password.'
    }

    throw error
  }
}

export async function loginWithTotp(code) {
  await request('/login/totp', {
    method: 'POST',
    body: JSON.stringify({ code }),
  })

  currentUser = await getCurrentUser()
  return currentUser
}

export async function setupTotp() {
  return request('/totp/setup', {
    method: 'POST',
  })
}

export async function verifyTotpSetup(code) {
  return request('/totp/verify-setup', {
    method: 'POST',
    body: JSON.stringify({ code }),
  })
}

export async function logout() {
  await request('/logout', {
    method: 'POST',
  })

  currentUser = null
}

export function getAuthenticatedUser() {
  return currentUser
}

export async function getCurrentUser() {
  const user = await request('/users/me')
  currentUser = user
  return user
}

export function changePassword(oldPassword, newPassword) {
  return request('/reset-password', {
    method: 'POST',
    body: JSON.stringify({
      oldPassword,
      newPassword,
    }),
  })
}

export function getPatientMedications() {
  return request('/medications/my')
}

export function getPatientLogs() {
  return request('/logs')
}

export function getPatientAlerts() {
  return request('/alerts')
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
  return request(`/doctor/patients/${encodeURIComponent(patientId)}`)
}

export function getMedication(patientId, medicationId) {
  return request(
    `/doctor/patients/${encodeURIComponent(patientId)}` +
      `/medications/${encodeURIComponent(medicationId)}`,
  )
}

export function addMedication(medication) {
  return request('/medications', {
    method: 'POST',
    body: JSON.stringify(medication),
  })
}

export function updateMedication(patientId, medicationId, medication) {
  return request(
    `/doctor/patients/${encodeURIComponent(patientId)}` +
      `/medications/${encodeURIComponent(medicationId)}`,
    {
      method: 'PUT',
      body: JSON.stringify(medication),
    },
  )
}

export function removeMedication(patientId, medicationId) {
  return request(
    `/doctor/patients/${encodeURIComponent(patientId)}` +
      `/medications/${encodeURIComponent(medicationId)}`,
    {
      method: 'DELETE',
    },
  )
}

export function getMedicationHistory() {
  return request('/doctor/medication-history')
}

export function getAdminUsers(role) {
  const query = role && role !== 'ALL' ? `?role=${encodeURIComponent(role)}` : ''

  return request(`/users${query}`)
}

export function registerUser(user) {
  return request('/users/register', {
    method: 'POST',
    body: JSON.stringify(user),
  })
}

export function updateAdminUser(id, user) {
  return request(`/users/${encodeURIComponent(id)}`, {
    method: 'PUT',
    body: JSON.stringify(user),
  })
}

export function updateAdminUserRole(id, role) {
  return request(`/users/${encodeURIComponent(id)}/role`, {
    method: 'PATCH',
    body: JSON.stringify({ role }),
  })
}

export function updateAdminUserStatus(id, active) {
  return request(`/users/${encodeURIComponent(id)}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ active }),
  })
}

export function getDoctorAssignedPatients(doctorId) {
  return request(`/users/${encodeURIComponent(doctorId)}/patients`)
}

export function updateDoctorPatientAssignments(doctorId, patientIds) {
  return request(`/users/${encodeURIComponent(doctorId)}/patients`, {
    method: 'PUT',
    body: JSON.stringify({ patientIds }),
  })
}

export function getSecurityLogs() {
  return request('/admin/security-logs')
}
