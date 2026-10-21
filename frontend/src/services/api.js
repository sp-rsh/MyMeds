const API_BASE = '/api'

let authHeader = null
let currentUsername = null
let currentUser = null

function encodeBasicCredentials(
  username,
  password
) {
  const bytes =
    new TextEncoder().encode(
      `${username}:${password}`
    )

  let binary = ''

  for (const byte of bytes) {
    binary +=
      String.fromCharCode(byte)
  }

  return `Basic ${btoa(binary)}`
}

async function request(
  url,
  options = {}
) {
  const response = await fetch(
    `${API_BASE}${url}`,
    {
      ...options,

      headers: {
        'Content-Type':
          'application/json',

        ...(authHeader && {
          Authorization:
            authHeader,
        }),

        ...options.headers,
      },
    }
  )

  if (!response.ok) {
    let message =
      `Request failed: ${response.status}`

    try {
      const contentType =
        response.headers.get(
          'content-type'
        )

      if (
        contentType?.includes(
          'application/json'
        )
      ) {
        const body =
          await response.json()

        message =
          body.message ??
          message
      } else {
        const body =
          await response.text()

        if (body) {
          message = body
        }
      }
    } catch {
    }

    const error =
      new Error(message)

    error.status =
      response.status

    throw error
  }

  if (response.status === 204) {
    return null
  }

  const contentType =
    response.headers.get(
      'content-type'
    )

  if (
    contentType?.includes(
      'application/json'
    )
  ) {
    return response.json()
  }

  return response.text()
}

export async function login(
  username,
  password
) {
  const header =
    encodeBasicCredentials(
      username,
      password
    )

  const response = await fetch(
    `${API_BASE}/home`,
    {
      headers: {
        Authorization: header,
      },
    }
  )

  if (!response.ok) {
    const error =
      new Error(
        'Invalid username or password.'
      )

    error.status =
      response.status

    throw error
  }

  authHeader = header
  currentUsername = username

  currentUser =
    await getCurrentUser()

  return currentUser
}

export function logout() {
  authHeader = null
  currentUsername = null
  currentUser = null
}

export function getAuthenticatedUser() {
  return currentUser
}

export async function getCurrentUser() {
  const user =
    await request('/users/me')

  currentUser = user

  return user
}

export async function changePassword(
  oldPassword,
  newPassword
) {
  const result =
    await request(
      '/reset-password',
      {
        method: 'POST',

        body: JSON.stringify({
          oldPassword,
          newPassword,
        }),
      }
    )

  if (currentUsername) {
    authHeader =
      encodeBasicCredentials(
        currentUsername,
        newPassword
      )
  }

  return result
}

export function getPatientMedications() {
  return request(
    '/medications/my'
  )
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
  return request(
    '/doctor/dashboard'
  )
}

export function getDoctorPatients() {
  return request(
    '/doctor/patients'
  )
}

export function getPatient(
  patientId
) {
  return request(
    `/doctor/patients/${encodeURIComponent(
      patientId
    )}`
  )
}

export function getMedication(
  patientId,
  medicationId
) {
  return request(
    `/doctor/patients/${encodeURIComponent(
      patientId
    )}` +
    `/medications/${encodeURIComponent(
      medicationId
    )}`
  )
}

export function addMedication(
  medication
) {
  return request(
    '/medications',
    {
      method: 'POST',

      body: JSON.stringify(
        medication
      ),
    }
  )
}

export function updateMedication(
  patientId,
  medicationId,
  medication
) {
  return request(
    `/doctor/patients/${encodeURIComponent(
      patientId
    )}` +
    `/medications/${encodeURIComponent(
      medicationId
    )}`,
    {
      method: 'PUT',

      body: JSON.stringify(
        medication
      ),
    }
  )
}

export function removeMedication(
  patientId,
  medicationId
) {
  return request(
    `/doctor/patients/${encodeURIComponent(
      patientId
    )}` +
    `/medications/${encodeURIComponent(
      medicationId
    )}`,
    {
      method: 'DELETE',
    }
  )
}

export function getMedicationHistory() {
  return request(
    '/doctor/medication-history'
  )
}

export function getAdminUsers(
  role
) {
  const query =
    role &&
    role !== 'ALL'
      ? `?role=${encodeURIComponent(
          role
        )}`
      : ''

  return request(
    `/users${query}`
  )
}

export function registerUser(
  user
) {
  return request(
    '/users/register',
    {
      method: 'POST',

      body:
        JSON.stringify(user),
    }
  )
}

export function updateAdminUser(
  id,
  user
) {
  return request(
    `/users/${encodeURIComponent(
      id
    )}`,
    {
      method: 'PUT',

      body:
        JSON.stringify(user),
    }
  )
}

export function updateAdminUserRole(
  id,
  role
) {
  return request(
    `/users/${encodeURIComponent(
      id
    )}/role`,
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
    `/users/${encodeURIComponent(
      id
    )}/status`,
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
    `/users/${encodeURIComponent(
      doctorId
    )}/patients`
  )
}

export function updateDoctorPatientAssignments(
  doctorId,
  patientIds
) {
  return request(
    `/users/${encodeURIComponent(
      doctorId
    )}/patients`,
    {
      method: 'PUT',

      body: JSON.stringify({
        patientIds,
      }),
    }
  )
}

export function getSecurityLogs() {
  return request(
    '/admin/security-logs'
  )
}
