package com.aliware.tasktracker.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task extends PanacheEntity {

    @NotBlank(message = "Title may not be blank")
    @Size(max = 100, message = "Title must be max 100 chars") // [cite: 40]
    @Column(nullable = false, length = 100)
    public String title;

    public String description; // [cite: 41]

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TaskStatus status; // 

    @CreationTimestamp
    @Column(updatable = false)
    public LocalDateTime createdAt; // [cite: 43]

    // Relación Many-to-One: Muchas tareas pertenecen a un usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user; // [cite: 44]
}