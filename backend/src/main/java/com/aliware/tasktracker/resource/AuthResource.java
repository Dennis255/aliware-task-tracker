package com.aliware.tasktracker.resource;

import com.aliware.tasktracker.dto.AuthRequest;
import com.aliware.tasktracker.dto.AuthResponse;
import com.aliware.tasktracker.model.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    // --- REGISTRO DE USUARIO ---
    @POST
    @Path("/register")
    @Transactional
    @PermitAll
    public Response register(@Valid AuthRequest request) { // @Valid activa Hibernate Validator
        // Usamos count() que es más eficiente que traer todo el objeto
        if (User.count("username", request.username()) > 0) {
            // Lanzamos excepción para que el GlobalExceptionHandler la procese
            throw new WebApplicationException("El nombre de usuario ya existe", 409);
        }

        User newUser = new User();
        newUser.username = request.username();
        newUser.password = BcryptUtil.bcryptHash(request.password());
        newUser.role = "USER";
        newUser.persist();

        // Devolvemos un JSON simple de éxito
        return Response.status(201).entity(new AuthResponse(null, "Usuario registrado exitosamente")).build();
    }

    // --- INICIO DE SESIÓN ---
    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid AuthRequest request) {
        User user = User.findByUsername(request.username());

        if (user == null || !BcryptUtil.matches(request.password(), user.password)) {
            // Error 401 estructurado
            throw new WebApplicationException("Credenciales inválidas", 401);
        }

        String token = Jwt.issuer("https://aliware.com/issuer")
                .upn(user.username)
                .groups(new HashSet<>(Set.of("USER")))
                .claim("id", user.id)
                .expiresIn(Duration.ofHours(8))
                .sign();

        return Response.ok(new AuthResponse(token, user.username)).build();
    }
}