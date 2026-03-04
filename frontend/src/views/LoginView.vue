<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card class="elevation-12 rounded-lg" :loading="loading">
          <!-- Header con gradiente y animación -->
          <v-toolbar color="primary" dark flat height="80" class="gradient-header">
            <v-toolbar-title class="text-h5 font-weight-bold d-flex align-center">
              <v-icon large class="mr-3" color="white">mdi-login</v-icon>
              Bienvenido de vuelta
            </v-toolbar-title>
          </v-toolbar>

          <v-card-text class="pa-6">
            <!-- Mensaje de bienvenida -->
            <div class="text-center mb-6">
              <v-avatar size="80" color="primary-lighten-5" class="mb-3">
                <v-icon size="48" color="primary">mdi-account-circle</v-icon>
              </v-avatar>
              <h3 class="text-h6 font-weight-medium">Inicia sesión en tu cuenta</h3>
              <p class="text-caption text-medium-emphasis">Ingresa tus credenciales para continuar</p>
            </div>

            <v-form @submit.prevent="handleLogin" ref="form" v-model="formValid">
              <!-- Campo Usuario mejorado -->
              <v-text-field
                v-model="username"
                label="Usuario"
                prepend-inner-icon="mdi-account"
                :rules="[rules.required]"
                required
                variant="outlined"
                density="comfortable"
                class="mb-4"
                color="primary"
                :error-messages="usernameError"
                @input="usernameError = ''"
                placeholder="Ingresa tu usuario"
                clearable
              >
                <template v-slot:prepend-inner>
                  <v-icon color="primary-lighten-2">mdi-account</v-icon>
                </template>
              </v-text-field>

              <!-- Campo Contraseña mejorado -->
              <v-text-field
                v-model="password"
                label="Contraseña"
                :type="showPassword ? 'text' : 'password'"
                :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showPassword = !showPassword"
                :rules="[rules.required]"
                required
                variant="outlined"
                density="comfortable"
                class="mb-2"
                color="primary"
                :error-messages="passwordError"
                @input="passwordError = ''"
                placeholder="Ingresa tu contraseña"
              >
                <template v-slot:prepend-inner>
                  <v-icon color="primary-lighten-2">mdi-lock</v-icon>
                </template>
              </v-text-field>

              <!-- Opciones adicionales (nuevas) -->
              <div class="d-flex align-center justify-space-between mb-4">
                

                <a 
                  href="#" 
                  @click.prevent="showForgotDialog = true" 
                  class="text-primary text-caption font-weight-medium text-decoration-none"
                >
                  ¿Olvidaste tu contraseña?
                </a>
              </div>
            </v-form>
          </v-card-text>

          <v-divider></v-divider>

          <v-card-actions class="pa-6">
            <v-btn
              color="primary"
              @click="handleLogin"
              :loading="loading"
              :disabled="loading"
              size="large"
              rounded="pill"
              elevation="2"
              class="px-8 font-weight-bold"
              block
              height="48"
            >
              <v-icon left class="mr-2">mdi-login-variant</v-icon>
              Iniciar Sesión
            </v-btn>
          </v-card-actions>

          <!-- Separador visual -->
          <div class="d-flex align-center px-6">
            <v-divider></v-divider>
            <span class="mx-4 text-caption text-medium-emphasis">o</span>
            <v-divider></v-divider>
          </div>

          <!-- Enlace a registro mejorado -->
          <v-card-text class="text-center pb-6">
            <div class="text-body-2 text-medium-emphasis">
              ¿No tienes una cuenta?
              <router-link to="/register" class="text-primary font-weight-medium text-decoration-none">
                Regístrate aquí
                <v-icon size="small">mdi-arrow-right</v-icon>
              </router-link>
            </div>
          </v-card-text>
        </v-card>

        <!-- Diálogo para recuperar contraseña (nuevo) -->
        <v-dialog v-model="showForgotDialog" max-width="400">
          <v-card>
            <v-card-title class="bg-primary text-white">
              <span class="text-h6">Recuperar Contraseña</span>
            </v-card-title>
            <v-card-text class="pa-6">
              <p class="text-body-2 mb-4">
                Ingresa tu correo electrónico y te enviaremos instrucciones para restablecer tu contraseña.
              </p>
              <v-text-field
                v-model="forgotEmail"
                label="Correo electrónico"
                prepend-inner-icon="mdi-email"
                variant="outlined"
                density="comfortable"
                color="primary"
              ></v-text-field>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="grey" variant="text" @click="showForgotDialog = false">
                Cancelar
              </v-btn>
              <v-btn 
                color="primary" 
                @click="showForgotDialog = false; showRecoverySnackbar = true"
              >
                Enviar
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- Snackbar mejorado para errores -->
        <v-snackbar
          v-model="snackbar"
          color="error"
          :timeout="4000"
          location="top"
          class="rounded-lg"
        >
          <div class="d-flex align-center">
            <v-icon color="white" class="mr-3">mdi-alert-circle</v-icon>
            <span class="font-weight-medium">{{ errorMessage }}</span>
          </div>
          <template v-slot:actions>
            <v-btn
              variant="text"
              icon="mdi-close"
              @click="snackbar = false"
              density="comfortable"
              color="white"
            ></v-btn>
          </template>
        </v-snackbar>

        <!-- Snackbar para recuperación (nuevo) -->
        <v-snackbar
          v-model="showRecoverySnackbar"
          color="success"
          :timeout="3000"
          location="top"
          class="rounded-lg"
        >
          <div class="d-flex align-center">
            <v-icon color="white" class="mr-3">mdi-email-check</v-icon>
            <span class="font-weight-medium">Se ha enviado un correo de recuperación</span>
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

// Variables originales
const username = ref('');
const password = ref('');
const loading = ref(false);
const snackbar = ref(false);
const errorMessage = ref('');

// Nuevas variables visuales
const formValid = ref(false);
const showPassword = ref(false);
const rememberMe = ref(false);
const showForgotDialog = ref(false);
const showRecoverySnackbar = ref(false);
const forgotEmail = ref('');
const usernameError = ref('');
const passwordError = ref('');
const form = ref(null);

// Reglas de validación visuales
const rules = {
  required: v => !!v || 'Este campo es requerido'
};

const authStore = useAuthStore();
const router = useRouter();

// Lógica original intacta
const handleLogin = async () => {
  // Validación visual rápida
  if (!username.value || !password.value) {
    if (!username.value) usernameError.value = 'Usuario requerido';
    if (!password.value) passwordError.value = 'Contraseña requerida';
    return;
  }

  loading.value = true;
  try {
    await authStore.login(username.value, password.value);
    router.push('/dashboard');
  } catch (error) {
    console.error(error);
    errorMessage.value = 'Credenciales inválidas o error de conexión';
    snackbar.value = true;
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.gradient-header {
  background: linear-gradient(45deg, #1976D2 0%, #0D47A1 100%) !important;
  border-radius: 12px 12px 0 0;
}

.v-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.v-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1) !important;
}

/* Estilos para campos de texto */
:deep(.v-field) {
  transition: all 0.3s ease;
}

:deep(.v-field--focused) {
  border-color: #1976D2 !important;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.1);
}

/* Avatar con degradado */
.v-avatar {
  background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
}

/* Animación para el botón */
.v-btn {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.v-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(25, 118, 210, 0.3) !important;
}
</style>