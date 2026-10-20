<template>
  <div class="admin-dashboard">
    <header class="page-header">
      <p class="eyebrow">
        ADMINISTRATION
      </p>

      <h1 class="page-title">
        Admin Dashboard
      </h1>

      <p class="page-subtitle">
        Manage MyMeds user accounts, roles, and doctor-patient assignments.
      </p>
    </header>

    <section class="action-grid">
      <RouterLink
        to="/admin/users"
        class="action-card"
      >
        <div class="action-icon blue">
          <Users :size="24" />
        </div>

        <div>
          <h2>
            Manage Users
          </h2>

          <p>
            Create accounts, update roles, and manage account access.
          </p>
        </div>

        <ChevronRight :size="20" />
      </RouterLink>

      <RouterLink
        to="/admin/doctors"
        class="action-card"
      >
        <div class="action-icon purple">
          <Stethoscope :size="24" />
        </div>

        <div>
          <h2>
            Manage Doctors
          </h2>

          <p>
            Manage doctor accounts and patient assignments.
          </p>
        </div>

        <ChevronRight :size="20" />
      </RouterLink>

      <RouterLink
        to="/admin/patients"
        class="action-card"
      >
        <div class="action-icon green">
          <UserRound :size="24" />
        </div>

        <div>
          <h2>
            Manage Patients
          </h2>

          <p>
            Manage patient accounts and assign doctors when needed.
          </p>
        </div>

        <ChevronRight :size="20" />
      </RouterLink>

      <RouterLink
        to="/admin/security"
        class="action-card"
      >
        <div class="action-icon orange">
          <ShieldCheck :size="24" />
        </div>

        <div>
          <h2>
            Security Logs
          </h2>

          <p>
            View security and administrative activity when audit logging is available.
          </p>
        </div>

        <ChevronRight :size="20" />
      </RouterLink>
    </section>

    <section class="admin-info">
      <div class="info-icon">
        <ShieldCheck :size="22" />
      </div>

      <div>
        <h2>
          Administrator permissions
        </h2>

        <p>
          Administrators manage accounts, roles, and doctor-patient assignments.
          Medication prescribing, dosage changes, and other clinical decisions
          remain under doctor control.
        </p>
      </div>
    </section>

    <section class="workflow-card">
      <div class="workflow-header">
        <div class="workflow-icon">
          <UserRound :size="22" />
        </div>

        <div>
          <h2>
            Patient assignment
          </h2>

          <p>
            Patients do not need to be assigned to a doctor immediately.
          </p>
        </div>
      </div>

      <div class="workflow-steps">
        <div class="workflow-step">
          <span>1</span>

          <div>
            <strong>
              Create patient account
            </strong>

            <p>
              Add the patient to MyMeds.
            </p>
          </div>
        </div>

        <div class="workflow-step">
          <span>2</span>

          <div>
            <strong>
              Assign a doctor
            </strong>

            <p>
              The doctor can be selected during creation or assigned later.
            </p>
          </div>
        </div>

        <div class="workflow-step">
          <span>3</span>

          <div>
            <strong>
              Doctor manages medication
            </strong>

            <p>
              Medication schedules and clinical changes remain doctor responsibilities.
            </p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import {
  Users,
  Stethoscope,
  UserRound,
  ShieldCheck,
  ChevronRight,
} from '@lucide/vue'
</script>

<style scoped>
.admin-dashboard {
  width: 100%;
}

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

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));

  gap: 18px;

  margin-bottom: 24px;
}

.action-card {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;

  gap: 16px;

  min-height: 130px;

  padding: 24px;

  background: #ffffff;

  border: 1px solid #e2e8f0;
  border-radius: 14px;

  color: inherit;
  text-decoration: none;

  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease,
    transform 0.15s ease;
}

.action-card:hover {
  border-color: #cbd8eb;

  box-shadow: 0 5px 18px rgba(23, 32, 51, 0.06);

  transform: translateY(-1px);
}

.action-icon {
  width: 50px;
  height: 50px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 12px;
}

.action-icon.blue {
  color: #2563eb;
  background: #eff6ff;
}

.action-icon.purple {
  color: #7c3aed;
  background: #f5f3ff;
}

.action-icon.green {
  color: #16a34a;
  background: #f0fdf4;
}

.action-icon.orange {
  color: #ea580c;
  background: #fff7ed;
}

.action-card h2 {
  margin: 0;

  color: #172033;

  font-size: 18px;
  font-weight: 700;
}

.action-card p {
  margin: 6px 0 0;

  color: #64748b;

  font-size: 14px;
  line-height: 1.5;
}

.action-card > svg {
  color: #94a3b8;
}

.admin-info {
  display: flex;
  align-items: flex-start;

  gap: 14px;

  margin-bottom: 24px;

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

  color: #2563eb;

  background: #ffffff;

  border-radius: 10px;
}

.admin-info h2 {
  margin: 0;

  color: #172033;

  font-size: 15px;
  font-weight: 700;
}

.admin-info p {
  margin: 5px 0 0;

  color: #64748b;

  font-size: 14px;
  line-height: 1.6;
}

.workflow-card {
  padding: 24px;

  background: #ffffff;

  border: 1px solid #e2e8f0;
  border-radius: 14px;
}

.workflow-header {
  display: flex;
  align-items: center;

  gap: 13px;

  margin-bottom: 22px;
}

.workflow-icon {
  width: 44px;
  height: 44px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 11px;

  background: #f0fdf4;
  color: #16a34a;
}

.workflow-header h2 {
  margin: 0;

  color: #172033;

  font-size: 18px;
}

.workflow-header p {
  margin: 5px 0 0;

  color: #64748b;

  font-size: 14px;
}

.workflow-steps {
  display: grid;
  grid-template-columns: repeat(3, 1fr);

  gap: 16px;
}

.workflow-step {
  display: flex;
  align-items: flex-start;

  gap: 12px;

  padding: 18px;

  background: #f8fafc;

  border: 1px solid #e2e8f0;
  border-radius: 12px;
}

.workflow-step > span {
  width: 30px;
  height: 30px;

  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;

  border-radius: 50%;

  background: #2563eb;
  color: #ffffff;

  font-size: 13px;
  font-weight: 700;
}

.workflow-step strong {
  color: #172033;

  font-size: 14px;
}

.workflow-step p {
  margin: 5px 0 0;

  color: #64748b;

  font-size: 13px;
  line-height: 1.5;
}

@media (max-width: 900px) {
  .action-grid {
    grid-template-columns: 1fr;
  }

  .workflow-steps {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .page-title {
    font-size: 32px;
  }

  .action-card {
    grid-template-columns: auto 1fr;
  }

  .action-card > svg {
    display: none;
  }
}
</style>
