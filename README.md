# 🚀 Aliware Task Tracker - Full Stack SPA

Este proyecto es una aplicación **Single Page Application (SPA)** multiusuario diseñada para la gestión segura y eficiente de tareas personales. Ha sido desarrollada bajo los estándares de una prueba técnica para **Aliware**, integrando un backend API REST profesional y un frontend reactivo.



## 🛠️ Stack Tecnológico Requerido
El proyecto utiliza las tecnologías más recientes recomendadas para entornos empresariales:

* **Backend:** Java 21 LTS, Quarkus Framework, Hibernate with Panache, Flyway (Migraciones), SmallRye JWT.
* **Frontend:** Node.js 22 LTS, Vue 3 (Composition API), Vuetify 3 (UI Components), Pinia (Estado), Vue Router.
* **DevOps & Herramientas:** Docker & Docker Compose, PostgreSQL 15, Nginx, JUnit 5 & REST-assured.

---

## 🚀 Instalación y Ejecución

### Opción 1: Mediante Docker Compose (Recomendado)
Levanta todo el ecosistema (Base de datos, Backend y Frontend) con un solo comando:
```bash
docker-compose up --build

Frontend: http://localhost:5173

Backend API: http://localhost:8080

Swagger UI: http://localhost:8080/q/swagger-ui (Documentación interactiva de la API).



API RESTful (Endpoints Protegidos)
La API sigue los principios REST y está protegida por JWT. Solo el dueño de una tarea puede visualizarla, editarla o eliminarla.

Método,Endpoint,Descripción,Acceso
POST,/api/auth/register,Registro de nuevos usuarios con password hasheado.,Público
POST,/api/auth/login,Autenticación y obtención de token JWT.,Público
GET,/api/tasks,Lista tareas del usuario con paginación y filtros.,Privado
POST,/api/tasks,Crea una tarea asignada automáticamente al usuario.,Privado
PUT,/api/tasks/{id},"Actualiza título, descripción o estado de una tarea.",Privado
DELETE,/api/tasks/{id},Elimina una tarea validando pertenencia.,Privado

Flujo de Usuario y Funcionalidades
Registro y Login: El sistema valida los inputs mediante Hibernate Validator (ej. email válido, contraseña mínima).

Gestión de Tareas: Al ingresar, el usuario accede a un Dashboard protegido por Navigation Guards de Vue Router.

Visualización Avanzada: Las tareas se muestran en una tabla de Vuetify (v-data-table-server) que consume la paginación directamente del servidor para mayor resiliencia.

Notificaciones: El sistema utiliza v-snackbar para confirmar el éxito o error de cada operación (CRUD).



Pruebas y Calidad
Para garantizar la integridad del sistema, se han implementado pruebas de integración:

Bash
cd backend
./mvnw test
Pruebas cubiertas: Registro de usuarios, login con JWT, acceso denegado a rutas protegidas y CRUD de tareas.

Manejo de Errores: Se implementó un manejador global que retorna errores en formato JSON estructurado (400, 401, 404, etc.).


Arquitectura del Proyecto
El proyecto sigue patrones de diseño modernos:

DTOs (Data Transfer Objects): Para la comunicación limpia entre capas.

Interceptores HTTP: Axios gestiona automáticamente el header Authorization: Bearer <token>.

Seguridad: Encriptación de contraseñas con BCrypt y validación de recursos a nivel de usuario.