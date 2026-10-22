<template>
  <div class="admin-page">

    <header class="page-header">
      <p class="eyebrow">ADMINISTRATION</p>

      <h1 class="page-title">
        System Settings
      </h1>

      <p class="page-subtitle">
        Configure system-level MyMeds settings and security controls.
      </p>
    </header>


    <div class="settings-grid">

      <!-- General -->

      <section class="panel">

        <div class="panel-header">
          <div class="panel-icon blue">
            <Settings :size="21" />
          </div>

          <div>
            <h2>General Settings</h2>
            <p>Basic application configuration.</p>
          </div>
        </div>


        <div class="setting-row">

          <div>
            <strong>System Name</strong>
            <span>Name displayed throughout the application.</span>
          </div>

          <input v-model="settings.systemName" />

        </div>


        <div class="setting-row">

          <div>
            <strong>Timezone</strong>
            <span>Timezone used for medication schedules and logs.</span>
          </div>

          <select v-model="settings.timezone">
            <option>Europe/Amsterdam</option>
            <option>Europe/London</option>
            <option>Asia/Kolkata</option>
          </select>

        </div>


        <div class="setting-row">

          <div>
            <strong>Maintenance Mode</strong>
            <span>
              Temporarily prevent normal users from accessing the system.
            </span>
          </div>

          <button
            class="toggle"
            :class="{ enabled: settings.maintenance }"
            @click="settings.maintenance = !settings.maintenance"
          >
            <span></span>
          </button>

        </div>

      </section>


      <!-- Security -->

      <section class="panel">

        <div class="panel-header">

          <div class="panel-icon purple">
            <ShieldCheck :size="21" />
          </div>

          <div>
            <h2>Security</h2>
            <p>Authentication and access-control settings.</p>
          </div>

        </div>


        <div class="setting-row">

          <div>
            <strong>Two-Factor Authentication</strong>
            <span>
              Require 2FA for administrative accounts.
            </span>
          </div>

          <button
            class="toggle"
            :class="{ enabled: settings.twoFactor }"
            @click="settings.twoFactor = !settings.twoFactor"
          >
            <span></span>
          </button>

        </div>


        <div class="setting-row">

          <div>
            <strong>Login Rate Limiting</strong>
            <span>
              Limit repeated unsuccessful login attempts.
            </span>
          </div>

          <button
            class="toggle"
            :class="{ enabled: settings.rateLimit }"
            @click="settings.rateLimit = !settings.rateLimit"
          >
            <span></span>
          </button>

        </div>


        <div class="setting-row">

          <div>
            <strong>Audit Logging</strong>
            <span>
              Record sensitive administrative and security actions.
            </span>
          </div>

          <button
            class="toggle"
            :class="{ enabled: settings.auditLogs }"
            @click="settings.auditLogs = !settings.auditLogs"
          >
            <span></span>
          </button>

        </div>

      </section>


      <!-- Device -->

      <section class="panel">

        <div class="panel-header">

          <div class="panel-icon green">
            <Cpu :size="21" />
          </div>

          <div>
            <h2>Device Configuration</h2>
            <p>MyMeds dispenser system settings.</p>
          </div>

        </div>


        <div class="setting-row">

          <div>
            <strong>Device Status</strong>
            <span>Connection status of the Raspberry Pi.</span>
          </div>

          <span class="connected">
            <span></span>
            Connected
          </span>

        </div>


        <div class="setting-row">

          <div>
            <strong>Automatic Dispensing</strong>
            <span>
              Allow scheduled medication dispensing.
            </span>
          </div>

          <button
            class="toggle"
            :class="{ enabled: settings.autoDispense }"
            @click="settings.autoDispense = !settings.autoDispense"
          >
            <span></span>
          </button>

        </div>


        <div class="setting-row">

          <div>
            <strong>Intrusion Monitoring</strong>
            <span>
              Monitor physical device security events.
            </span>
          </div>

          <button
            class="toggle"
            :class="{ enabled: settings.intrusion }"
            @click="settings.intrusion = !settings.intrusion"
          >
            <span></span>
          </button>

        </div>

      </section>


      <!-- Save -->

      <section class="save-panel">

        <div>
          <h3>Save system configuration</h3>

          <p>
            Changes made here are system-level settings and
            should only be modified by authorized administrators.
          </p>
        </div>

        <button
          class="primary-button"
          @click="saveSettings"
        >
          <Save :size="18" />
          Save Changes
        </button>

      </section>

    </div>


    <section class="info-box">

      <div class="info-icon">
        <ShieldCheck :size="21" />
      </div>

      <div>
        <h3>Least-privilege administration</h3>

        <p>
          System settings should not provide administrators with
          clinical control over medication dosage, frequency, or
          patient prescriptions.
        </p>
      </div>

    </section>

  </div>
</template>


<script setup>
import { reactive } from 'vue'

import {
  Settings,
  ShieldCheck,
  Cpu,
  Save,
} from '@lucide/vue'


const settings = reactive({
  systemName: 'MyMeds',
  timezone: 'Europe/Amsterdam',
  maintenance: false,

  twoFactor: true,
  rateLimit: true,
  auditLogs: true,

  autoDispense: true,
  intrusion: true,
})


function saveSettings() {
  alert('System settings saved successfully.')
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

.settings-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.panel {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 13px;
  padding: 22px;
  border-bottom: 1px solid #f1f5f9;
}

.panel-icon {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}

.panel-icon.blue {
  color: #2563eb;
  background: #eff6ff;
}

.panel-icon.purple {
  color: #7c3aed;
  background: #f5f3ff;
}

.panel-icon.green {
  color: #16a34a;
  background: #f0fdf4;
}

.panel-header h2 {
  margin: 0;
  color: #172033;
  font-size: 17px;
}

.panel-header p {
  margin: 4px 0 0;
  color: #94a3b8;
  font-size: 12px;
}

.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 18px 22px;
  border-bottom: 1px solid #f1f5f9;
}

.setting-row:last-child {
  border-bottom: none;
}

.setting-row strong {
  display: block;
  color: #172033;
  font-size: 13px;
}

.setting-row div span {
  display: block;
  margin-top: 4px;
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.4;
}

.setting-row input,
.setting-row select {
  width: 180px;
  height: 38px;
  padding: 0 10px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  outline: none;
  color: #172033;
  background: #ffffff;
}

.toggle {
  position: relative;
  width: 44px;
  height: 24px;
  padding: 0;
  border: none;
  border-radius: 999px;
  background: #cbd5e1;
  cursor: pointer;
  flex-shrink: 0;
}

.toggle span {
  position: absolute;
  top: 3px;
  left: 3px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #ffffff;
  transition: .2s;
}

.toggle.enabled {
  background: #2563eb;
}

.toggle.enabled span {
  left: 23px;
}

.connected {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  color: #15803d;
  font-size: 12px;
  font-weight: 600;
}

.connected > span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #22c55e;
}

.save-panel {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 22px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
}

.save-panel h3 {
  margin: 0;
  color: #172033;
  font-size: 14px;
}

.save-panel p {
  margin: 5px 0 0;
  color: #64748b;
  font-size: 13px;
}

.primary-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 11px 17px;
  border: none;
  border-radius: 9px;
  background: #2563eb;
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
}

.primary-button:hover {
  background: #1d4ed8;
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

@media (max-width: 900px) {
  .settings-grid {
    grid-template-columns: 1fr;
  }

  .save-panel {
    grid-column: auto;
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>