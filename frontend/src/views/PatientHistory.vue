<script setup>
import {
  Pill,
  CheckCircle2,
  Clock3,
  CalendarDays,
} from '@lucide/vue'

const history = [
  {
    date: 'Today',
    events: [
      {
        time: '08:02',
        name: 'Vitamin D',
        dosage: '1 tablet',
        description: 'Medication collected',
        type: 'completed',
      },
      {
        time: '13:00',
        name: 'Metformin',
        dosage: '2 tablets',
        description: 'Scheduled',
        type: 'scheduled',
      },
    ],
  },
  {
    date: 'Yesterday',
    events: [
      {
        time: '08:04',
        name: 'Vitamin D',
        dosage: '1 tablet',
        description: 'Medication collected',
        type: 'completed',
      },
    ],
  },
]
</script>

<template>
  <section class="history-page">
    <header class="page-header">
      <h1 class="page-title">Medication History</h1>
      <p class="page-subtitle">
        View your recent medication activity and dispensing history.
      </p>
    </header>

    <section class="history-section">
      <div
        v-for="day in history"
        :key="day.date"
        class="day-section"
      >
        <div class="day-heading">
          <CalendarDays :size="19" />
          <h2>{{ day.date }}</h2>
        </div>

        <div class="history-list">
          <article
            v-for="event in day.events"
            :key="`${day.date}-${event.time}-${event.name}`"
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
              <strong>{{ event.time }}</strong>
            </div>

            <div class="medication-icon">
              <Pill :size="21" />
            </div>

            <div class="event-info">
              <h3>{{ event.name }}</h3>
              <p>{{ event.dosage }}</p>
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

    <div class="info-bar">
      <Pill :size="20" />

      <p>
        Medication history is automatically recorded when medication is
        dispensed or collected. This information helps you and your care team
        monitor medication adherence.
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
  font-weight: 700;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.history-card {
  display: grid;
  grid-template-columns: 18px 70px 44px 1fr auto;
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
  align-items: flex-start;
  justify-content: center;
}

.timeline-dot {
  position: relative;
  z-index: 2;

  width: 10px;
  height: 10px;

  margin-top: 5px;

  border-radius: 50%;
  background: #2563eb;
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
  font-size: 15px;
  font-weight: 600;
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
  font-size: 16px;
  font-weight: 650;
}

.event-info p {
  margin: 4px 0 0;

  color: #64748b;
  font-size: 14px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;

  padding: 7px 11px;

  border-radius: 999px;

  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
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
  align-items: flex-start;
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
  font-size: 14px;
  line-height: 1.5;
}

@media (max-width: 800px) {
  .history-card {
    grid-template-columns: 18px 60px 44px 1fr;
  }

  .event-status {
    grid-column: 4;
  }
}

@media (max-width: 600px) {
  .history-card {
    grid-template-columns: 18px 1fr;
    gap: 10px 14px;
  }

  .timeline {
    grid-row: span 3;
  }

  .event-time,
  .medication-icon,
  .event-info,
  .event-status {
    grid-column: 2;
  }

  .medication-icon {
    display: none;
  }

  .event-status {
    margin-top: 2px;
  }
}
</style>