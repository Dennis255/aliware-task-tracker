import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

// Importaremos los componentes (que crearemos en el siguiente paso)
// Usamos importación dinámica para mejorar rendimiento (Lazy Loading)
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/DashboardView.vue'),
      meta: { requiresAuth: true } // Marca esta ruta como protegida
    }
  ]
});

// --- NAVIGATION GUARD (Protección de Rutas) ---
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login'); // Si no está logueado y quiere ir al dashboard -> Login
  } else {
    next(); // Continuar
  }
});

export default router;