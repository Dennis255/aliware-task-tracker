package com.aliware.tasktracker.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        
        // Caso 1: Errores de validación (Hibernate Validator) -> 400 Bad Request
        if (exception instanceof ConstraintViolationException) {
            errorResponse.put("error", "Validación fallida");
            errorResponse.put("message", exception.getMessage()); // Podrías formatear esto mejor si quisieras
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

        // Caso 2: Errores HTTP estándar de Quarkus (401, 403, 404 lanzados manualmente)
        if (exception instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) exception;
            errorResponse.put("error", "Error HTTP");
            errorResponse.put("message", exception.getMessage());
            return Response.status(webEx.getResponse().getStatus()).entity(errorResponse).build();
        }

        // Caso 3: Error inesperado (NullPointer, Base de datos caída, etc.) -> 500
        errorResponse.put("error", "Error Interno del Servidor");
        errorResponse.put("message", exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }
}