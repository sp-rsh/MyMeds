<template>
  <div class="unauthorized-page">
    <div class="card">
      <h1>Access Denied</h1>

      <p>
        You do not have permission to access this page.
      </p>

      <RouterLink to="/login">
        Return to Login
      </RouterLink>
    </div>
  </div>
</template>

<style scoped>
.unauthorized-page {
  min-height: 100vh;

  display: flex;
  align-items: center;
  justify-content: center;

  padding: 30px;
}

.card {
  width: 100%;
  max-width: 480px;

  padding: 40px;

  background: #ffffff;

  border: 1px solid #e2e8f0;
  border-radius: 14px;

  text-align: center;
}

h1 {
  margin: 0 0 12px;

  color: #172033;

  font-size: 28px;
}

p {
  margin: 0 0 24px;

  color: #64748b;

  font-size: 16px;
}

a {
  display: inline-block;

  padding: 10px 18px;

  border-radius: 8px;

  background: #2563eb;
  color: #ffffff;

  text-decoration: none;
  font-weight: 600;
}

a:hover {
  background: #1d4ed8;
}
</style>