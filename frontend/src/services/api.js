import axios from 'axios';

// Creamos la instancia apuntando a tu Backend Quarkus
const api = axios.create({
  baseURL: 'http://localhost:8080/api', // Asegúrate de que este puerto coincida con tu Quarkus
  headers: {
    'Content-Type': 'application/json',
  },
});

// --- INTERCEPTOR DE PETICIONES (Request) ---
// Antes de enviar la petición, le pegamos el token si existe
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// --- INTERCEPTOR DE RESPUESTAS (Response) ---
// Si el backend nos responde 401 (No autorizado), borramos todo y mandamos al login
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login'; // Redirección forzada
    }
    return Promise.reject(error);
  }
);

export default api;