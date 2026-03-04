package com.aliware.tasktracker.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@UserDefinition
public class User extends PanacheEntity {

    @Username
    @Column(nullable = false, unique = true)
    public String username; // [cite: 36]

    @Password
    @Column(nullable = false)
    public String password; // [cite: 37]

    @Roles
    public String role; // Campo auxiliar para roles (opcional pero recomendado para JWT)

    // Método helper para buscar por username
    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }
}