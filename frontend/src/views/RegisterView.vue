<template>
  <v-container class="fill-height auth-bg" fluid>
    <!-- Decorative orbs -->
    <div class="orb orb-1"></div>
    <div class="orb orb-2"></div>
    <div class="orb orb-3"></div>

    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <div class="glass-card">
          <!-- Header -->
          <div class="card-header">
            <div class="brand-icon">
              <v-icon size="28" color="white">mdi-leaf</v-icon>
            </div>
            <div>
              <h1 class="card-title">Crear cuenta</h1>
              <p class="card-subtitle">Únete hoy, es gratis</p>
            </div>
          </div>

          <!-- Form -->
          <div class="card-body">
            <v-form @submit.prevent="handleRegister">
              <div class="field-wrapper">
                <label class="field-label">Usuario</label>
                <div class="input-group">
                  <v-icon class="input-icon" size="18">mdi-account-outline</v-icon>
                  <input
                    v-model="username"
                    type="text"
                    class="custom-input"
                    placeholder="Tu nombre de usuario"
                    required
                  />
                </div>
              </div>

              <div class="field-wrapper">
                <label class="field-label">Contraseña</label>
                <div class="input-group">
                  <v-icon class="input-icon" size="18">mdi-lock-outline</v-icon>
                  <input
                    v-model="password"
                    type="password"
                    class="custom-input"
                    placeholder="Mínimo 8 caracteres"
                    required
                  />
                </div>
              </div>

              <button
                class="submit-btn"
                @click="handleRegister"
                :disabled="loading"
                type="button"
              >
                <span v-if="!loading" class="btn-content">
                  <v-icon size="18" class="mr-2">mdi-check-circle-outline</v-icon>
                  Registrarse
                </span>
                <span v-else class="btn-content">
                  <v-progress-circular indeterminate size="18" width="2" color="white" class="mr-2" />
                  Procesando...
                </span>
              </button>
            </v-form>
          </div>

          <!-- Footer -->
          <div class="card-footer">
            <span class="footer-text">¿Ya tienes cuenta?</span>
            <router-link to="/login" class="footer-link">Inicia Sesión</router-link>
          </div>
        </div>

        <v-snackbar v-model="snackbar" :color="snackbarColor" rounded="pill" location="top">
          <div class="d-flex align-center gap-2">
            <v-icon size="18">{{ snackbarColor === 'success' ? 'mdi-check-circle' : 'mdi-alert-circle' }}</v-icon>
            {{ message }}
          </div>
        </v-snackbar>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';

const username = ref('');
const password = ref('');
const loading = ref(false);
const snackbar = ref(false);
const message = ref('');
const snackbarColor = ref('success');

const authStore = useAuthStore();
const router = useRouter();

const handleRegister = async () => {
  loading.value = true;
  try {
    await authStore.register(username.value, password.value);
    message.value = '¡Registro exitoso! Redirigiendo...';
    snackbarColor.value = 'success';
    snackbar.value = true;
    setTimeout(() => router.push('/login'), 1500);
  } catch (error) {
    console.error(error);
    message.value = 'Error al registrar. El usuario podría ya existir.';
    snackbarColor.value = 'error';
    snackbar.value = true;
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@300;400;500;600&family=Sora:wght@600;700&display=swap');

/* ── Background ── */
.auth-bg {
  background: #0b1a12;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  font-family: 'DM Sans', sans-serif;
}

/* ── Decorative orbs ── */
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
  opacity: 0.35;
}
.orb-1 {
  width: 420px; height: 420px;
  background: radial-gradient(circle, #1a6b35, transparent 70%);
  top: -120px; left: -100px;
  animation: drift 12s ease-in-out infinite alternate;
}
.orb-2 {
  width: 300px; height: 300px;
  background: radial-gradient(circle, #0d4a22, transparent 70%);
  bottom: -80px; right: -60px;
  animation: drift 9s ease-in-out infinite alternate-reverse;
}
.orb-3 {
  width: 180px; height: 180px;
  background: radial-gradient(circle, #2d9e56, transparent 70%);
  top: 45%; right: 20%;
  animation: drift 15s ease-in-out infinite alternate;
}
@keyframes drift {
  from { transform: translate(0, 0) scale(1); }
  to   { transform: translate(30px, 20px) scale(1.06); }
}

/* ── Glass card ── */
.glass-card {
  background: rgba(255, 255, 255, 0.04);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.09);
  border-radius: 20px;
  box-shadow:
    0 0 0 1px rgba(45, 158, 86, 0.12),
    0 24px 64px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255,255,255,0.07);
  overflow: hidden;
  animation: fadeUp 0.55s cubic-bezier(0.22, 1, 0.36, 1) both;
}
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(28px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ── Card header ── */
.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 28px 28px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}
.brand-icon {
  width: 52px; height: 52px;
  border-radius: 14px;
  background: linear-gradient(135deg, #1e8c45, #0f5228);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 20px rgba(30, 140, 69, 0.4);
  flex-shrink: 0;
}
.card-title {
  font-family: 'Sora', sans-serif;
  font-size: 1.35rem;
  font-weight: 700;
  color: #f0faf4;
  margin: 0;
  line-height: 1.2;
}
.card-subtitle {
  font-size: 0.8rem;
  color: rgba(160, 210, 175, 0.6);
  margin: 3px 0 0;
  letter-spacing: 0.02em;
}

/* ── Card body ── */
.card-body {
  padding: 24px 28px;
}

/* ── Field ── */
.field-wrapper {
  margin-bottom: 18px;
}
.field-label {
  display: block;
  font-size: 0.75rem;
  font-weight: 500;
  color: rgba(160, 210, 175, 0.7);
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 8px;
}
.input-group {
  position: relative;
  display: flex;
  align-items: center;
}
.input-icon {
  position: absolute;
  left: 14px;
  color: rgba(100, 200, 130, 0.5) !important;
  pointer-events: none;
  z-index: 1;
}
.custom-input {
  width: 100%;
  padding: 12px 14px 12px 42px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  color: #e8f5ec;
  font-family: 'DM Sans', sans-serif;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.2s, background 0.2s, box-shadow 0.2s;
}
.custom-input::placeholder {
  color: rgba(160, 210, 175, 0.3);
}
.custom-input:focus {
  border-color: rgba(45, 158, 86, 0.6);
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 0 0 3px rgba(45, 158, 86, 0.12);
}

/* ── Submit button ── */
.submit-btn {
  width: 100%;
  margin-top: 8px;
  padding: 13px 20px;
  background: linear-gradient(135deg, #1e8c45 0%, #0f6630 100%);
  border: none;
  border-radius: 10px;
  color: #fff;
  font-family: 'DM Sans', sans-serif;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  letter-spacing: 0.02em;
  transition: transform 0.15s, box-shadow 0.15s, opacity 0.15s;
  box-shadow: 0 4px 18px rgba(30, 140, 69, 0.35);
}
.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 8px 28px rgba(30, 140, 69, 0.5);
}
.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}
.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn-content {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* ── Card footer ── */
.card-footer {
  padding: 16px 28px 24px;
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}
.footer-text {
  font-size: 0.85rem;
  color: rgba(160, 210, 175, 0.5);
  margin-right: 6px;
}
.footer-link {
  font-size: 0.85rem;
  font-weight: 600;
  color: #3ecf6e;
  text-decoration: none;
  transition: color 0.2s;
}
.footer-link:hover {
  color: #6edda0;
  text-decoration: underline;
}
</style>