<template>
  <div class="admin-dashboard">

    <!-- Header -->
    <header class="page-header">
      <p class="eyebrow">ADMINISTRATION</p>

      <h1 class="page-title">
        Admin Dashboard
      </h1>

      <p class="page-subtitle">
        Overview of users, system status, and recent administrative activity.
      </p>
    </header>


    <!-- Overview Cards -->
    <section class="overview-grid">

      <div class="overview-card">
        <div class="card-icon blue">
          <Users :size="22" />
        </div>

        <div>
          <p class="card-label">TOTAL USERS</p>
          <p class="card-value">24</p>
        </div>
      </div>


      <div class="overview-card">
        <div class="card-icon purple">
          <Stethoscope :size="22" />
        </div>

        <div>
          <p class="card-label">ACTIVE DOCTORS</p>
          <p class="card-value">6</p>
        </div>
      </div>


      <div class="overview-card">
        <div class="card-icon green">
          <UserRound :size="22" />
        </div>

        <div>
          <p class="card-label">ACTIVE PATIENTS</p>
          <p class="card-value">18</p>
        </div>
      </div>


      <div class="overview-card">
        <div class="card-icon green">
          <CheckCircle2 :size="22" />
        </div>

        <div>
          <p class="card-label">SYSTEM STATUS</p>

          <p class="card-value status-value">
            Operational
          </p>
        </div>
      </div>

    </section>


    <!-- Main Grid -->
    <section class="main-grid">

      <!-- System Status -->
      <div class="panel">

        <div class="panel-header">
          <div>
            <h2>System Status</h2>

            <p>
              Current status of MyMeds services.
            </p>
          </div>

          <div class="status-pill">
            <span></span>
            Operational
          </div>
        </div>


        <div class="status-list">

          <div class="status-row">
            <div class="status-left">
              <div class="status-icon">
                <Cpu :size="19" />
              </div>

              <div>
                <strong>Raspberry Pi</strong>
                <span>Medication dispenser</span>
              </div>
            </div>

            <span class="online">
              Online
            </span>
          </div>


          <div class="status-row">
            <div class="status-left">
              <div class="status-icon">
                <Database :size="19" />
              </div>

              <div>
                <strong>Database</strong>
                <span>Application data storage</span>
              </div>
            </div>

            <span class="online">
              Connected
            </span>
          </div>


          <div class="status-row">
            <div class="status-left">
              <div class="status-icon">
                <ShieldCheck :size="19" />
              </div>

              <div>
                <strong>Authentication</strong>
                <span>Login and access control</span>
              </div>
            </div>

            <span class="online">
              Operational
            </span>
          </div>

        </div>

      </div>


      <!-- Recent Activity -->
      <div class="panel">

        <div class="panel-header">
          <div>
            <h2>Recent Activity</h2>

            <p>
              Latest administrative events.
            </p>
          </div>

          <Activity :size="21" class="header-icon" />
        </div>


        <div class="activity-list">

          <div class="activity-item">
            <div class="activity-icon">
              <UserPlus :size="18" />
            </div>

            <div class="activity-content">
              <strong>Patient account created</strong>
              <span>2 minutes ago</span>
            </div>
          </div>


          <div class="activity-item">
            <div class="activity-icon">
              <UserCheck :size="18" />
            </div>

            <div class="activity-content">
              <strong>Patient assigned to doctor</strong>
              <span>18 minutes ago</span>
            </div>
          </div>


          <div class="activity-item">
            <div class="activity-icon">
              <ShieldCheck :size="18" />
            </div>

            <div class="activity-content">
              <strong>User role updated</strong>
              <span>42 minutes ago</span>
            </div>
          </div>


          <div class="activity-item">
            <div class="activity-icon">
              <ClipboardCheck :size="18" />
            </div>

            <div class="activity-content">
              <strong>Medication activity recorded</strong>
              <span>1 hour ago</span>
            </div>
          </div>

        </div>

      </div>

    </section>


    <!-- Admin Permission Information -->
    <section class="permission-info">

      <div class="permission-icon">
        <ShieldCheck :size="22" />
      </div>

      <div>
        <h3>Administrator permissions</h3>

        <p>
          Admin permissions are limited to account, role, system,
          and security administration. Medication prescribing and
          clinical changes remain under doctor control.
        </p>
      </div>

    </section>

  </div>
</template>


<script setup>
import {
  Users,
  Stethoscope,
  UserRound,
  CheckCircle2,
  Cpu,
  Database,
  ShieldCheck,
  Activity,
  UserPlus,
  UserCheck,
  ClipboardCheck,
} from '@lucide/vue'
</script>


<style scoped>
.admin-dashboard {
  width: 100%;
}


/* Header */

.page-header {
  margin-bottom: 32px;
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
  line-height: 1.5;
}


/* Overview */

.overview-grid {
  display: grid;

  grid-template-columns:
    repeat(4, minmax(0, 1fr));

  gap: 18px;

  margin-bottom: 24px;
}

.overview-card {
  display: flex;
  align-items: center;

  gap: 15px;

  min-height: 120px;

  padding: 22px;

  background: #ffffff;

  border: 1px solid #e2e8f0;

  border-radius: 14px;
}

.card-icon {
  width: 48px;
  height: 48px;

  display: flex;
  align-items: center;
  justify-content: center;

  border-radius: 12px;

  flex-shrink: 0;
}

.card-icon.blue {
  color: #2563eb;
  background: #eff6ff;
}

.card-icon.purple {
  color: #7c3aed;
  background: #f5f3ff;
}

.card-icon.green {
  color: #16a34a;
  background: #f0fdf4;
}

.card-label {
  margin: 0 0 5px;

  color: #94a3b8;

  font-size: 11px;
  font-weight: 700;

  letter-spacing: 0.8px;
}

.card-value {
  margin: 0;

  color: #172033;

  font-size: 27px;
  font-weight: 700;
}

.status-value {
  font-size: 21px;
}


/* Main panels */

.main-grid {
  display: grid;

  grid-template-columns:
    minmax(0, 1.15fr)
    minmax(0, 0.85fr);

  gap: 24px;
}

.panel {
  background: #ffffff;

  border: 1px solid #e2e8f0;

  border-radius: 14px;

  padding: 26px;
}

.panel-header {
  display: flex;

  align-items: flex-start;
  justify-content: space-between;

  margin-bottom: 22px;
}

.panel-header h2 {
  margin: 0;

  color: #172033;

  font-size: 19px;
  font-weight: 700;
}

.panel-header p {
  margin: 6px 0 0;

  color: #64748b;

  font-size: 14px;
}

.header-icon {
  color: #94a3b8;
}


/* Status */

.status-pill {
  display: flex;
  align-items: center;
  gap: 7px;

  padding: 7px 10px;

  border-radius: 999px;

  color: #15803d;
  background: #f0fdf4;

  font-size: 12px;
  font-weight: 600;
}

.status-pill span {
  width: 7px;
  height: 7px;

  border-radius: 50%;

  background: #22c55e;
}

.status-list {
  display: flex;
  flex-direction: column;
}

.status-row {
  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 15px 0;

  border-top: 1px solid #f1f5f9;
}

.status-left {
  display: flex;
  align-items: center;

  gap: 12px;
}

.status-icon {
  width: 40px;
  height: 40px;

  display: flex;
  align-items: center;
  justify-content: center;

  color: #2563eb;
  background: #eff6ff;

  border-radius: 9px;
}

.status-left strong {
  display: block;

  color: #172033;

  font-size: 14px;
  font-weight: 600;
}

.status-left span {
  display: block;

  margin-top: 3px;

  color: #94a3b8;

  font-size: 12px;
}

.online {
  color: #16a34a;

  font-size: 13px;
  font-weight: 600;
}


/* Activity */

.activity-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  align-items: center;

  gap: 12px;

  padding: 14px 0;

  border-top: 1px solid #f1f5f9;
}

.activity-icon {
  width: 38px;
  height: 38px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  color: #2563eb;
  background: #eff6ff;

  border-radius: 9px;
}

.activity-content strong {
  display: block;

  color: #172033;

  font-size: 13px;
  font-weight: 600;
}

.activity-content span {
  display: block;

  margin-top: 3px;

  color: #94a3b8;

  font-size: 12px;
}


/* Permission information */

.permission-info {
  display: flex;
  align-items: flex-start;

  gap: 14px;

  margin-top: 24px;

  padding: 20px 22px;

  background: #eff6ff;

  border: 1px solid #dbeafe;

  border-radius: 14px;
}

.permission-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 42px;
  height: 42px;

  flex-shrink: 0;

  color: #2563eb;

  background: #ffffff;

  border-radius: 10px;
}

.permission-info h3 {
  margin: 0;

  color: #172033;

  font-size: 14px;
  font-weight: 700;
}

.permission-info p {
  margin: 5px 0 0;

  color: #64748b;

  font-size: 13px;
  line-height: 1.5;
}


/* Responsive */

@media (max-width: 1100px) {
  .overview-grid {
    grid-template-columns:
      repeat(2, minmax(0, 1fr));
  }

  .main-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 700px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: 32px;
  }
}
</style>