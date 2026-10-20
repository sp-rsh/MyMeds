<script setup>
import {
  computed,
  onMounted,
  ref,
} from 'vue'

import {
  Pill,
  CheckCircle2,
  Clock3,
  CalendarDays,
  CircleAlert,
  LoaderCircle,
} from '@lucide/vue'

import {
  getPatientLogs,
} from '../services/api'


const history = ref([])

const loading = ref(true)
const errorMessage = ref('')


async function loadHistory() {
  loading.value = true
  errorMessage.value = ''

  try {
    const result =
      await getPatientLogs()

    const logs =
      getArrayResult(result)

    history.value =
      logs.map(normalizeLog)

  } catch (error) {
    console.error(
      'Failed to load medication history:',
      error
    )

    history.value = []

    errorMessage.value =
      getErrorMessage(error)

  } finally {
    loading.value = false
  }
}


function normalizeLog(log, index) {
  const timestamp =
    log.timestamp ??
    log.createdAt ??
    log.dispensedAt ??
    log.dateTime ??
    log.time ??
    ''

  const status =
    String(
      log.status ??
      log.activity ??
      log.description ??
      ''
    ).toLowerCase()

  const completed =
    status.includes('taken') ||
    status.includes('collect') ||
    status.includes('dispens') ||
    status.includes('complete')

  return {
    id:
      log.id ??
      log.logId ??
      index,

    timestamp,

    date:
      getDateLabel(timestamp),

    time:
      getTimeLabel(timestamp),

    name:
      log.medicationName ??
      log.medication?.name ??
      log.name ??
      'Medication',

    dosage:
      log.dosage ??
      log.medication?.dosage ??
      '',

    description:
      log.description ??
      log.activity ??
      log.status ??
      'Recorded',

    type:
      completed
        ? 'completed'
        : 'scheduled',
  }
}


const groupedHistory = computed(() => {
  const groups = []

  for (const event of history.value) {
    let group =
      groups.find(
        item =>
          item.date === event.date
      )

    if (!group) {
      group = {
        date: event.date,
        events: [],
      }

      groups.push(group)
    }

    group.events.push(event)
  }

  return groups
})


function getArrayResult(result) {
  if (Array.isArray(result)) {
    return result
  }

  if (Array.isArray(result?.content)) {
    return result.content
  }

  if (Array.isArray(result?.logs)) {
    return result.logs
  }

  return []
}


function getDateLabel(value) {
  const date =
    new Date(value)

  if (Number.isNaN(date.getTime())) {
    return 'Medication Activity'
  }

  const today =
    new Date()

  const yesterday =
    new Date()

  yesterday.setDate(
    today.getDate() - 1
  )

  if (sameDate(date, today)) {
    return 'Today'
  }

  if (sameDate(date, yesterday)) {
    return 'Yesterday'
  }

  return date.toLocaleDateString()
}


function getTimeLabel(value) {
  const date =
    new Date(value)

  if (!Number.isNaN(date.getTime())) {
    return date.toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const text =
    String(value || '')

  if (/^\d{2}:\d{2}/.test(text)) {
    return text.slice(0, 5)
  }

  return '—'
}


function sameDate(a, b) {
  return (
    a.getFullYear() === b.getFullYear() &&
    a.getMonth() === b.getMonth() &&
    a.getDate() === b.getDate()
  )
}


function getErrorMessage(error) {
  if (error?.status === 401) {
    return 'Authentication is required.'
  }

  if (error?.status === 403) {
    return 'You do not have permission to view medication history.'
  }

  return 'Medication history is currently unavailable.'
}


onMounted(loadHistory)
</script>


<template>
  <section class="history-page">

    <header class="page-header">

      <h1 class="page-title">
        Medication History
      </h1>

      <p class="page-subtitle">
        View your recent medication activity and dispensing history.
      </p>

    </header>


    <div
      v-if="errorMessage"
      class="error-box"
    >
      <CircleAlert :size="20" />

      <div>
        <strong>
          Unable to load history
        </strong>

        <p>
          {{ errorMessage }}
        </p>
      </div>

      <button @click="loadHistory">
        Retry
      </button>
    </div>


    <div
      v-if="loading"
      class="loading-state"
    >
      <LoaderCircle
        :size="30"
        class="loading-icon"
      />

      Loading medication history...
    </div>


    <section
      v-else-if="groupedHistory.length"
      class="history-section"
    >

      <div
        v-for="day in groupedHistory"
        :key="day.date"
        class="day-section"
      >

        <div class="day-heading">

          <CalendarDays :size="19" />

          <h2>
            {{ day.date }}
          </h2>

        </div>


        <div class="history-list">

          <article
            v-for="event in day.events"
            :key="event.id"
            class="history-card"
          >

            <div class="timeline">

              <div
                class="timeline-dot"
                :class="event.type"
              ></div>

              <div class="timeline-line"></div>

            </div>


            <div class="event-time">
              <strong>
                {{ event.time }}
              </strong>
            </div>


            <div class="medication-icon">
              <Pill :size="21" />
            </div>


            <div class="event-info">

              <h3>
                {{ event.name }}
              </h3>

              <p v-if="event.dosage">
                {{ event.dosage }}
              </p>

            </div>


            <div class="event-status">

              <span
                class="status-badge"
                :class="event.type"
              >

                <CheckCircle2
                  v-if="event.type === 'completed'"
                  :size="15"
                />

                <Clock3
                  v-else
                  :size="15"
                />

                {{ event.description }}

              </span>

            </div>

          </article>

        </div>

      </div>

    </section>


    <div
      v-else-if="!errorMessage && !loading"
      class="empty-state"
    >
      <CalendarDays :size="30" />

      <strong>
        No medication history available
      </strong>

      <span>
        Recorded medication activity will appear here.
      </span>
    </div>


    <div class="info-bar">

      <Pill :size="20" />

      <p>
        Medication history is automatically recorded when medication
        activity is registered. This information helps you and your
        care team monitor adherence.
      </p>

    </div>

  </section>
</template>


<style scoped>
.history-page {
  max-width: 1200px;
}

.page-header {
  margin-bottom: 34px;
}

.history-section {
  display: flex;
  flex-direction: column;
  gap: 34px;
}

.day-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.day-heading {
  display: flex;
  align-items: center;
  gap: 9px;
  color: #172033;
}

.day-heading svg {
  color: #2563eb;
}

.day-heading h2 {
  margin: 0;
  font-size: 19px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.history-card {
  display: grid;
  grid-template-columns:
    18px
    70px
    44px
    1fr
    auto;

  align-items: center;
  gap: 16px;

  min-height: 76px;
  padding: 15px 20px;

  background: #ffffff;
  border: 1px solid #e0e6ee;
  border-radius: 15px;
}

.timeline {
  position: relative;
  width: 18px;
  height: 48px;
  display: flex;
  justify-content: center;
}

.timeline-dot {
  position: relative;
  z-index: 2;
  width: 10px;
  height: 10px;
  margin-top: 5px;
  border-radius: 50%;
}

.timeline-dot.completed {
  background: #16a34a;
}

.timeline-dot.scheduled {
  background: #2563eb;
}

.timeline-line {
  position: absolute;
  top: 15px;
  bottom: -23px;
  width: 1px;
  background: #dbe3ee;
}

.history-card:last-child .timeline-line {
  display: none;
}

.event-time strong {
  color: #172033;
}

.medication-icon {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 11px;
  background: #f1f5f9;
  color: #475569;
}

.event-info h3 {
  margin: 0;
  color: #172033;
}

.event-info p {
  margin: 4px 0 0;
  color: #64748b;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 11px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
}

.status-badge.completed {
  background: #ecfdf3;
  color: #15803d;
}

.status-badge.scheduled {
  background: #eaf2ff;
  color: #2563eb;
}

.info-bar {
  display: flex;
  gap: 12px;
  margin-top: 28px;
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

.error-box {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding: 15px 17px;
  border: 1px solid #fecaca;
  border-radius: 12px;
  background: #fef2f2;
  color: #b91c1c;
}

.error-box div {
  flex: 1;
}

.error-box p {
  margin: 3px 0 0;
  font-size: 13px;
}

.error-box button {
  padding: 7px 12px;
  border: 1px solid #fecaca;
  border-radius: 7px;
  background: white;
  color: #b91c1c;
  cursor: pointer;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 50px 20px;
  color: #94a3b8;
  text-align: center;
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

@media (max-width: 800px) {
  .history-card {
    grid-template-columns:
      18px
      60px
      44px
      1fr;
  }

  .event-status {
    grid-column: 4;
  }
}
</style>
