package com.aliware.tasktracker.resource;

import com.aliware.tasktracker.dto.PaginatedResponse;
import com.aliware.tasktracker.dto.TaskRequest;
import com.aliware.tasktracker.dto.TaskResponse;
import com.aliware.tasktracker.model.Task;
import com.aliware.tasktracker.model.TaskStatus;
import com.aliware.tasktracker.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.Authenticated;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class TaskResource {

    @Inject
    JsonWebToken jwt;

    // --- LISTAR TAREAS ---
    @GET
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("10") int pageSize,
            @QueryParam("status") String statusStr
    ) {
        Long userId = Long.parseLong(jwt.getClaim("id").toString());

        PanacheQuery<Task> query;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                TaskStatus status = TaskStatus.valueOf(statusStr.toUpperCase());
                query = Task.find("user.id = ?1 and status = ?2", Sort.descending("createdAt"), userId, status);
            } catch (IllegalArgumentException e) {
                // Si el estado no es válido, ignoramos el filtro o podríamos lanzar 400
                query = Task.find("user.id", Sort.descending("createdAt"), userId);
            }
        } else {
            query = Task.find("user.id", Sort.descending("createdAt"), userId);
        }

        List<TaskResponse> tasks = query.page(Page.of(pageIndex, pageSize))
                .list().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        long totalCount = query.count();

        return Response.ok(new PaginatedResponse<>(tasks, totalCount)).build();
    }

    // --- CREAR TAREA ---
    @POST
    @Transactional
    public Response create(@Valid TaskRequest request) { // Validamos el input
        Long userId = Long.parseLong(jwt.getClaim("id").toString());
        User user = User.findById(userId);

        if (user == null) {
            throw new WebApplicationException("Usuario no encontrado", 404);
        }

        Task task = new Task();
        task.title = request.title();
        task.description = request.description();
        
        // Convertir String (DTO) a Enum (Modelo)
        try {
            task.status = (request.status() != null) 
                ? TaskStatus.valueOf(request.status().toUpperCase()) 
                : TaskStatus.PENDING;
        } catch (IllegalArgumentException e) {
             throw new WebApplicationException("Estado inválido. Valores permitidos: PENDING, IN_PROGRESS, COMPLETED", 400);
        }
        
        task.user = user;
        task.persist();

        return Response.status(201).entity(toResponse(task)).build();
    }

    // --- ACTUALIZAR TAREA ---
    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, @Valid TaskRequest request) {
        Long userId = Long.parseLong(jwt.getClaim("id").toString());
        
        Task task = Task.findById(id);

        if (task == null) {
            throw new WebApplicationException("Tarea no encontrada", 404);
        }

        if (!task.user.id.equals(userId)) {
            throw new WebApplicationException("Acceso denegado: Esta tarea no te pertenece", 403);
        }

        task.title = request.title();
        task.description = request.description();
        
        if (request.status() != null) {
             try {
                task.status = TaskStatus.valueOf(request.status().toUpperCase());
            } catch (IllegalArgumentException e) {
                 throw new WebApplicationException("Estado inválido", 400);
            }
        }
        
        return Response.ok(toResponse(task)).build();
    }

    // --- ELIMINAR TAREA ---
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Long userId = Long.parseLong(jwt.getClaim("id").toString());

        Task task = Task.findById(id);

        if (task == null) {
             throw new WebApplicationException("Tarea no encontrada", 404);
        }

        if (!task.user.id.equals(userId)) {
             throw new WebApplicationException("Acceso denegado: Esta tarea no te pertenece", 403);
        }

        task.delete();
        return Response.noContent().build();
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
            task.id, 
            task.title, 
            task.description, 
            task.status, 
            task.createdAt
        );
    }
}