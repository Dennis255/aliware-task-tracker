import { defineStore } from 'pinia';
import api from '../services/api';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token,
  },

  actions: {
    async login(username, password) {
      try {
        // Hacemos POST al endpoint que creamos en Quarkus
        const response = await api.post('/auth/login', { username, password });
        
        // Guardamos los datos
        this.token = response.data.token;
        this.user = { username: response.data.username };
        
        localStorage.setItem('token', this.token);
        localStorage.setItem('user', JSON.stringify(this.user));
        
        return true; // Login exitoso
      } catch (error) {
        console.error("Login error:", error);
        throw error;
      }
    },

    async register(username, password) {
      await api.post('/auth/register', { username, password });
    },

    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // Redirigimos usando el router en el componente, no aquí para mantenerlo limpio
    }
  }
});