<template>
  <div class="admin-page">

    <header class="page-header">
      <div>
        <p class="eyebrow">ADMINISTRATION</p>
        <h1 class="page-title">Security Logs</h1>
        <p class="page-subtitle">
          Monitor authentication, access, and administrative events.
        </p>
      </div>
    </header>


    <!-- Summary -->

    <section class="summary-grid">

      <div class="summary-card">
        <div class="summary-icon blue">
          <Activity :size="21" />
        </div>

        <div>
          <span>Events Today</span>
          <strong>{{ logs.length }}</strong>
        </div>
      </div>


      <div class="summary-card">
        <div class="summary-icon green">
          <CheckCircle2 :size="21" />
        </div>

        <div>
          <span>Successful</span>
          <strong>{{ successfulEvents }}</strong>
        </div>
      </div>


      <div class="summary-card">
        <div class="summary-icon red">
          <AlertTriangle :size="21" />
        </div>

        <div>
          <span>Security Alerts</span>
          <strong>{{ alerts }}</strong>
        </div>
      </div>

    </section>


    <!-- Filters -->

    <section class="panel">

      <div class="toolbar">

        <div class="search-box">
          <Search :size="19" />

          <input
            v-model="searchQuery"
            placeholder="Search security events..."
          />
        </div>


        <select v-model="typeFilter">
          <option value="All">All event types</option>
          <option value="Login">Login</option>
          <option value="Role Change">Role Change</option>
          <option value="Account">Account</option>
          <option value="Access">Access</option>
          <option value="Security">Security</option>
        </select>


        <select v-model="resultFilter">
          <option value="All">All results</option>
          <option value="Success">Success</option>
          <option value="Warning">Warning</option>
          <option value="Blocked">Blocked</option>
        </select>

      </div>


      <!-- Logs -->

      <div class="table-wrapper">

        <table>

          <thead>
            <tr>
              <th>Time</th>
              <th>Event</th>
              <th>User</th>
              <th>Type</th>
              <th>Result</th>
              <th>Details</th>
            </tr>
          </thead>


          <tbody>

            <tr
              v-for="log in filteredLogs"
              :key="log.id"
            >

              <td class="time">
                {{ log.time }}
              </td>


              <td>
                <strong class="event">
                  {{ log.event }}
                </strong>
              </td>


              <td>
                <span class="user">
                  {{ log.user }}
                </span>
              </td>


              <td>
                <span class="type-badge">
                  {{ log.type }}
                </span>
              </td>


              <td>

                <span
                  class="result"
                  :class="getResultClass(log.result)"
                >
                  <span></span>
                  {{ log.result }}
                </span>

              </td>


              <td>
                <span class="details">
                  {{ log.details }}
                </span>
              </td>

            </tr>

          </tbody>

        </table>

      </div>

    </section>


    <!-- Security explanation -->

    <section class="info-box">

      <div class="info-icon">
        <ShieldCheck :size="21" />
      </div>

      <div>
        <h3>Security monitoring</h3>

        <p>
          Security logs provide an audit trail for authentication,
          role changes, account administration, and blocked access
          attempts. Sensitive actions should be recorded with the
          responsible user and timestamp.
        </p>
      </div>

    </section>

  </div>
</template>


<script setup>
import { computed, ref } from 'vue'

import {
  Activity,
  CheckCircle2,
  AlertTriangle,
  Search,
  ShieldCheck,
} from '@lucide/vue'


const logs = ref([
  {
    id: 1,
    time: '10:22',
    event: 'Admin login',
    user: 'System Administrator',
    type: 'Login',
    result: 'Success',
    details: '2FA verified',
  },
  {
    id: 2,
    time: '10:05',
    event: 'Doctor login',
    user: 'Dr. Sharma',
    type: 'Login',
    result: 'Success',
    details: 'Successful authentication',
  },
  {
    id: 3,
    time: '09:54',
    event: 'Role updated',
    user: 'System Administrator',
    type: 'Role Change',
    result: 'Success',
    details: 'Patient role confirmed',
  },
  {
    id: 4,
    time: '09:41',
    event: 'Patient account created',
    user: 'System Administrator',
    type: 'Account',
    result: 'Success',
    details: 'New patient account',
  },
  {
    id: 5,
    time: '09:17',
    event: 'Unauthorized access attempt',
    user: 'Unknown',
    type: 'Access',
    result: 'Blocked',
    details: 'Admin endpoint denied',
  },
  {
    id: 6,
    time: '08:56',
    event: 'Multiple failed logins',
    user: 'Unknown',
    type: 'Security',
    result: 'Warning',
    details: '5 failed attempts',
  },
  {
    id: 7,
    time: '08:32',
    event: 'Doctor login',
    user: 'Dr. Mehta',
    type: 'Login',
    result: 'Success',
    details: 'Successful authentication',
  },
])


const searchQuery = ref('')
const typeFilter = ref('All')
const resultFilter = ref('All')


const filteredLogs = computed(() => {

  const query = searchQuery.value
    .trim()
    .toLowerCase()

  return logs.value.filter((log) => {

    const matchesSearch =
      !query ||
      log.event.toLowerCase().includes(query) ||
      log.user.toLowerCase().includes(query) ||
      log.details.toLowerCase().includes(query)

    const matchesType =
      typeFilter.value === 'All' ||
      log.type === typeFilter.value

    const matchesResult =
      resultFilter.value === 'All' ||
      log.result === resultFilter.value

    return (
      matchesSearch &&
      matchesType &&
      matchesResult
    )
  })
})


const successfulEvents = computed(() =>
  logs.value.filter(
    log => log.result === 'Success'
  ).length
)


const alerts = computed(() =>
  logs.value.filter(
    log =>
      log.result === 'Warning' ||
      log.result === 'Blocked'
  ).length
)


function getResultClass(result) {
  if (result === 'Success') {
    return 'success'
  }

  if (result === 'Warning') {
    return 'warning'
  }

  return 'blocked'
}
</script>


<style scoped>
.admin-page {
  width: 100%;
}

.page-header {
  margin-bottom: 30px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1.2px;
}

.page-title {
  margin: 0;
  color: #172033;
  font-size: 38px;
  font-weight: 700;
  letter-spacing: -1px;
}

.page-subtitle {
  margin: 9px 0 0;
  color: #64748b;
  font-size: 17px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
  margin-bottom: 24px;
}

.summary-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 13px;
}

.summary-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}

.summary-icon.blue {
  color: #2563eb;
  background: #eff6ff;
}

.summary-icon.green {
  color: #16a34a;
  background: #f0fdf4;
}

.summary-icon.red {
  color: #dc2626;
  background: #fef2f2;
}

.summary-card span {
  display: block;
  color: #94a3b8;
  font-size: 12px;
}

.summary-card strong {
  display: block;
  margin-top: 3px;
  color: #172033;
  font-size: 24px;
}

.panel {
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
}

.toolbar {
  display: flex;
  gap: 12px;
  padding: 18px 20px;
  border-bottom: 1px solid #e2e8f0;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 420px;
}

.search-box svg {
  position: absolute;
  top: 50%;
  left: 13px;
  transform: translateY(-50%);
  color: #94a3b8;
}

.search-box input {
  width: 100%;
  height: 42px;
  padding: 0 14px 0 41px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  outline: none;
}

.toolbar select {
  height: 42px;
  padding: 0 30px 0 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #ffffff;
  color: #475569;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  min-width: 900px;
  border-collapse: collapse;
}

th {
  padding: 14px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  color: #64748b;
  font-size: 11px;
  text-align: left;
  text-transform: uppercase;
  letter-spacing: .7px;
}

td {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.time,
.user,
.details {
  color: #64748b;
  font-size: 13px;
}

.event {
  color: #172033;
  font-size: 13px;
}

.type-badge {
  padding: 6px 9px;
  border-radius: 7px;
  background: #f1f5f9;
  color: #475569;
  font-size: 11px;
  font-weight: 600;
}

.result {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  font-size: 12px;
  font-weight: 600;
}

.result > span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}

.result.success {
  color: #15803d;
}

.result.success > span {
  background: #22c55e;
}

.result.warning {
  color: #c2410c;
}

.result.warning > span {
  background: #f97316;
}

.result.blocked {
  color: #dc2626;
}

.result.blocked > span {
  background: #ef4444;
}

.info-box {
  display: flex;
  gap: 14px;
  margin-top: 24px;
  padding: 20px 22px;
  background: #eff6ff;
  border: 1px solid #dbeafe;
  border-radius: 14px;
}

.info-icon {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: #ffffff;
  color: #2563eb;
  border-radius: 10px;
}

.info-box h3 {
  margin: 0;
  font-size: 14px;
}

.info-box p {
  margin: 5px 0 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.5;
}

@media (max-width: 800px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
  }

  .search-box {
    max-width: none;
  }
}
</style>