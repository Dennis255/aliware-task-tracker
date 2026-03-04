<template>
  <v-container class="pa-6">
    <!-- Header mejorado con gradiente y sombra -->
    <v-row class="mb-6" align="center">
      <v-col>
        <div class="d-flex align-center">
          <v-avatar size="56" color="primary" class="mr-4 elevation-4">
            <v-icon size="32" color="white">mdi-format-list-checkbox</v-icon>
          </v-avatar>
          <div>
            <h1 class="text-h3 font-weight-bold gradient-text">Gestor de Tareas</h1>
            <p class="text-subtitle-1 text-medium-emphasis mt-1">Organiza y da seguimiento a tus actividades</p>
          </div>
        </div>
      </v-col>
      <v-col class="text-right">
        <v-btn 
          color="error" 
          variant="outlined" 
          @click="logout" 
          prepend-icon="mdi-logout"
          size="large"
          rounded="pill"
          class="font-weight-medium"
          elevation="2"
        >
          Cerrar Sesión
        </v-btn>
      </v-col>
    </v-row>

    <!-- Tarjeta para crear tareas mejorada -->
    <v-card class="mb-8 pa-6 rounded-lg" elevation="4">
      <v-row align="center">
        <v-col cols="12" md="5">
          <v-text-field 
            v-model="newTask.title" 
            label="¿Qué necesitas hacer?" 
            placeholder="Ej: Comprar víveres"
            hide-details="auto"
            density="comfortable"
            variant="outlined"
            prepend-inner-icon="mdi-format-title"
            color="primary"
            clearable
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="5">
          <v-text-field 
            v-model="newTask.description" 
            label="Descripción detallada" 
            placeholder="Agrega más información sobre la tarea"
            hide-details="auto"
            density="comfortable"
            variant="outlined"
            prepend-inner-icon="mdi-text-box"
            color="primary"
            clearable
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="2">
          <v-btn 
            color="primary" 
            block 
            @click="createTask" 
            :loading="creating" 
            :disabled="!newTask.title"
            size="large"
            rounded="pill"
            elevation="3"
            height="48"
            class="font-weight-bold"
          >
            <v-icon left class="mr-2">mdi-plus-circle</v-icon>
            Crear Tarea
          </v-btn>
        </v-col>
      </v-row>
    </v-card>

    <!-- Pestañas de filtro con diseño mejorado -->
    <v-card class="mb-6 rounded-lg" elevation="2">
      <v-tabs 
        v-model="filterStatus" 
        color="primary" 
        grow
        show-arrows
        class="px-4"
      >
        <v-tab value="" class="text-subtitle-2 font-weight-medium">
          <v-icon left class="mr-2">mdi-format-list-bulleted</v-icon>
          Todas
        </v-tab>
        <v-tab value="PENDING" class="text-subtitle-2 font-weight-medium">
          <v-icon left class="mr-2" color="grey">mdi-clock-outline</v-icon>
          Pendientes
        </v-tab>
        <v-tab value="IN_PROGRESS" class="text-subtitle-2 font-weight-medium">
          <v-icon left class="mr-2" color="orange">mdi-progress-clock</v-icon>
          En Progreso
        </v-tab>
        <v-tab value="COMPLETED" class="text-subtitle-2 font-weight-medium">
          <v-icon left class="mr-2" color="green">mdi-check-circle-outline</v-icon>
          Completadas
        </v-tab>
      </v-tabs>
    </v-card>

    <!-- Tabla de tareas mejorada -->
    <v-card class="rounded-lg" elevation="3">
      <v-data-table-server
        v-model:items-per-page="itemsPerPage"
        :headers="headers"
        :items="tasks"
        :items-length="totalTasks"
        :loading="loading"
        :search="search"
        class="elevation-0"
        @update:options="loadItems"
        hover
        :items-per-page-options="[5, 10, 25, 50]"
      >
        <!-- Columna Título con icono -->
        <template v-slot:[`item.title`]="{ item }">
          <div class="d-flex align-center">
            <v-icon 
              :color="getStatusColor(item.status)" 
              size="small" 
              class="mr-2"
            >
              {{ getStatusIcon(item.status) }}
            </v-icon>
            <span class="font-weight-medium">{{ item.title }}</span>
          </div>
        </template>

        <!-- Columna Descripción con límite de texto -->
        <template v-slot:[`item.description`]="{ item }">
          <div class="text-truncate" style="max-width: 250px;">
            <span v-if="item.description" class="text-body-2">
              {{ item.description }}
            </span>
            <span v-else class="text-grey text-caption italic">Sin descripción</span>
          </div>
        </template>

        <!-- Columna Estado con chips mejorados -->
        <template v-slot:[`item.status`]="{ item }">
          <v-chip 
            :color="getStatusColor(item.status)" 
            size="small" 
            class="font-weight-bold px-3"
            :prepend-icon="getStatusIcon(item.status)"
            variant="elevated"
          >
            {{ formatStatus(item.status) }}
          </v-chip>
        </template>

        <!-- Columna Acciones con botones en lugar de iconos (para mejor clickeabilidad) -->
        <template v-slot:[`item.actions`]="{ item }">
          <div class="d-flex gap-2">
            <v-tooltip text="Editar tarea" location="top">
              <template v-slot:activator="{ props }">
                <v-btn
                  v-bind="props"
                  icon="mdi-pencil"
                  size="small"
                  color="primary"
                  variant="text"
                  @click="openEditDialog(item)"
                  class="mr-1"
                ></v-btn>
              </template>
            </v-tooltip>
            <v-tooltip text="Eliminar tarea" location="top">
              <template v-slot:activator="{ props }">
                <v-btn
                  v-bind="props"
                  icon="mdi-delete"
                  size="small"
                  color="error"
                  variant="text"
                  @click="deleteTask(item.id)"
                ></v-btn>
              </template>
            </v-tooltip>
          </div>
        </template>

        <!-- Estado vacío mejorado -->
        <template v-slot:no-data>
          <div class="text-center pa-8">
            <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-clipboard-text-outline</v-icon>
            <h3 class="text-h6 font-weight-medium text-grey-darken-1">No hay tareas</h3>
            <p class="text-body-2 text-grey">Comienza creando una nueva tarea usando el formulario superior</p>
          </div>
        </template>

        <!-- Estado de carga personalizado -->
        <template v-slot:loading>
          <div class="text-center pa-8">
            <v-progress-circular
              indeterminate
              color="primary"
              size="48"
              width="4"
            ></v-progress-circular>
            <p class="text-body-2 text-grey mt-4">Cargando tareas...</p>
          </div>
        </template>
      </v-data-table-server>
    </v-card>

    <!-- Diálogo de edición mejorado -->
    <v-dialog v-model="dialogEdit" max-width="600px" transition="dialog-bottom-transition">
      <v-card class="rounded-lg">
        <v-card-title class="bg-primary text-white text-h5 pa-4">
          <v-icon left class="mr-2">mdi-pencil-box</v-icon>
          Editar Tarea
        </v-card-title>
        
        <v-card-text class="pa-6">
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field 
                  v-model="editedTask.title" 
                  label="Título de la tarea"
                  variant="outlined"
                  prepend-inner-icon="mdi-format-title"
                  color="primary"
                  hint="Máximo 100 caracteres"
                  counter="100"
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-textarea 
                  v-model="editedTask.description" 
                  label="Descripción detallada"
                  variant="outlined"
                  prepend-inner-icon="mdi-text-box"
                  color="primary"
                  rows="3"
                  auto-grow
                ></v-textarea>
              </v-col>
              <v-col cols="12">
                <v-select
                  v-model="editedTask.status"
                  :items="['PENDING', 'IN_PROGRESS', 'COMPLETED']"
                  label="Estado de la tarea"
                  variant="outlined"
                  prepend-inner-icon="mdi-state-machine"
                  color="primary"
                >
                  <template v-slot:item="{ props, item }">
                    <v-list-item v-bind="props">
                      <template v-slot:prepend>
                        <v-icon :color="getStatusColor(item.value)" class="mr-2">
                          {{ getStatusIcon(item.value) }}
                        </v-icon>
                      </template>
                    </v-list-item>
                  </template>
                  <template v-slot:selection="{ item }">
                    <div class="d-flex align-center">
                      <v-icon :color="getStatusColor(item.value)" size="small" class="mr-2">
                        {{ getStatusIcon(item.value) }}
                      </v-icon>
                      <span>{{ formatStatus(item.value) }}</span>
                    </div>
                  </template>
                </v-select>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>

        <v-card-actions class="pa-4 bg-grey-lighten-4">
          <v-spacer></v-spacer>
          <v-btn 
            color="grey-darken-1" 
            variant="text" 
            @click="dialogEdit = false"
            size="large"
            rounded="pill"
          >
            <v-icon left class="mr-1">mdi-close</v-icon>
            Cancelar
          </v-btn>
          <v-btn 
            color="primary" 
            variant="elevated" 
            @click="updateTask"
            size="large"
            rounded="pill"
            elevation="2"
            class="px-6"
          >
            <v-icon left class="mr-1">mdi-content-save</v-icon>
            Guardar Cambios
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar mejorado con iconos -->
    <v-snackbar 
      v-model="snackbar" 
      :color="snackbarColor" 
      timeout="4000"
      location="top"
      class="rounded-lg"
    >
      <div class="d-flex align-center">
        <v-icon :color="snackbarColor === 'success' ? 'white' : 'white'" class="mr-3">
          {{ snackbarColor === 'success' ? 'mdi-check-circle' : 'mdi-alert-circle' }}
        </v-icon>
        <span class="font-weight-medium">{{ snackbarText }}</span>
      </div>
      <template #actions>
        <v-btn
          variant="text"
          icon="mdi-close"
          @click="snackbar = false"
          density="comfortable"
          color="white"
        ></v-btn>
      </template>
    </v-snackbar>

    <!-- Diálogo de confirmación para eliminar (nuevo) -->
    <v-dialog v-model="deleteDialog" max-width="400px">
      <v-card>
        <v-card-title class="bg-error text-white">
          <v-icon left class="mr-2">mdi-alert</v-icon>
          Confirmar Eliminación
        </v-card-title>
        <v-card-text class="pa-6 text-center">
          <v-icon size="64" color="error" class="mb-4">mdi-delete-alert</v-icon>
          <p class="text-body-1 mb-2">¿Estás seguro de eliminar esta tarea?</p>
          <p class="text-caption text-medium-emphasis">Esta acción no se puede deshacer</p>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="deleteDialog = false">Cancelar</v-btn>
          <v-btn color="error" variant="elevated" @click="confirmDelete">Eliminar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, watch } from 'vue';
import api from '../services/api';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';

// Estado de datos
const tasks = ref([]);
const totalTasks = ref(0);
const loading = ref(false);
const creating = ref(false);
const search = ref('');
const filterStatus = ref('');
const itemsPerPage = ref(10);
const currentPage = ref(1);

// Estado de UI
const dialogEdit = ref(false);
const snackbar = ref(false);
const snackbarText = ref('');
const snackbarColor = ref('success');
const deleteDialog = ref(false);
const taskToDelete = ref(null);

// Modelos
const newTask = ref({ title: '', description: '', status: 'PENDING' });
const editedTask = ref({ id: null, title: '', description: '', status: '' });

const authStore = useAuthStore();
const router = useRouter();

const headers = [
  { title: 'Título', key: 'title', align: 'start' },
  { title: 'Descripción', key: 'description', sortable: false },
  { title: 'Estado', key: 'status', align: 'center' },
  { title: 'Acciones', key: 'actions', sortable: false, align: 'right' },
];

// --- API: Cargar Tareas (Paginadas) ---
const loadItems = async ({ page, itemsPerPage, sortBy }) => {
  loading.value = true;
  currentPage.value = page; // Guardamos la página actual
  try {
    const params = {
      page: page - 1, // Backend usa índice 0, Vuetify usa 1
      size: itemsPerPage,
      status: filterStatus.value || undefined
    };
    
    const response = await api.get('/tasks', { params });
    
    // Asignamos usando la nueva estructura del Backend { items: [], total: 0 }
    tasks.value = response.data.items;
    totalTasks.value = response.data.total;
  } catch (error) {
    showSnackbar('Error al cargar tareas', 'error');
  } finally {
    loading.value = false;
  }
};

// --- Crear Tarea ---
const createTask = async () => {
  if (!newTask.value.title) return;
  creating.value = true;
  try {
    await api.post('/tasks', newTask.value);
    showSnackbar('Tarea creada exitosamente');
    newTask.value = { title: '', description: '', status: 'PENDING' };
    // Recargar la tabla
    loadItems({ page: 1, itemsPerPage: itemsPerPage.value });
  } catch (error) {
    showSnackbar('Error al crear tarea', 'error');
  } finally {
    creating.value = false;
  }
};

// --- Editar Tarea ---
const openEditDialog = (item) => {
  editedTask.value = { ...item }; // Copia para no editar directo en tabla
  dialogEdit.value = true;
};

const updateTask = async () => {
  try {
    await api.put(`/tasks/${editedTask.value.id}`, editedTask.value);
    showSnackbar('Tarea actualizada');
    dialogEdit.value = false;
    loadItems({ page: currentPage.value, itemsPerPage: itemsPerPage.value });
  } catch (error) {
    showSnackbar('Error al actualizar', 'error');
  }
};

// --- Eliminar Tarea (mejorado) ---
const deleteTask = async (id) => {
  taskToDelete.value = id;
  deleteDialog.value = true;
};

const confirmDelete = async () => {
  try {
    await api.delete(`/tasks/${taskToDelete.value}`);
    showSnackbar('Tarea eliminada');
    loadItems({ page: currentPage.value, itemsPerPage: itemsPerPage.value });
  } catch (error) {
    showSnackbar('Error al eliminar', 'error');
  } finally {
    deleteDialog.value = false;
    taskToDelete.value = null;
  }
};

// --- Logout ---
const logout = () => {
  authStore.logout();
  router.push('/login');
};

// --- Helpers ---
const showSnackbar = (text, color = 'success') => {
  snackbarText.value = text;
  snackbarColor.value = color;
  snackbar.value = true;
};

const getStatusColor = (status) => {
  const colors = {
    'PENDING': 'grey',
    'IN_PROGRESS': 'orange',
    'COMPLETED': 'green'
  };
  return colors[status] || 'grey';
};

const getStatusIcon = (status) => {
  const icons = {
    'PENDING': 'mdi-clock-outline',
    'IN_PROGRESS': 'mdi-progress-clock',
    'COMPLETED': 'mdi-check-circle-outline'
  };
  return icons[status] || 'mdi-circle';
};

const formatStatus = (status) => {
  const labels = {
    'PENDING': 'Pendiente',
    'IN_PROGRESS': 'En Progreso',
    'COMPLETED': 'Completada'
  };
  return labels[status] || status;
};

// Recargar cuando cambia el filtro (Tabs)
watch(filterStatus, () => {
  loadItems({ page: 1, itemsPerPage: itemsPerPage.value });
});
</script>

<style scoped>
.gradient-text {
  background: linear-gradient(45deg, #1976D2, #4CAF50);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.v-card {
  transition: all 0.3s ease;
}

.v-card:hover {
  transform: translateY(-2px);
}

.v-btn {
  transition: all 0.2s ease;
}

.v-btn:hover {
  transform: translateY(-2px);
}

.gap-2 {
  gap: 8px;
}

.italic {
  font-style: italic;
}

/* Animaciones para el diálogo */
.dialog-bottom-transition-enter-active,
.dialog-bottom-transition-leave-active {
  transition: transform 0.3s ease;
}

.dialog-bottom-transition-enter-from,
.dialog-bottom-transition-leave-to {
  transform: translateY(50px);
}

/* Estilo para las filas de la tabla */
:deep(.v-data-table__tr:hover) {
  background-color: rgba(25, 118, 210, 0.05) !important;
  cursor: pointer;
}

/* Mejoras para los chips */
:deep(.v-chip) {
  transition: all 0.2s ease;
}

:deep(.v-chip:hover) {
  transform: scale(1.05);
}
</style>