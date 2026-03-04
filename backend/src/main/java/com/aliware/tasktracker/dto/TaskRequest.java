package com.aliware.tasktracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TaskRequest(
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    String title,

    String description,

    @Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED", message = "Estado inválido")
    String status
) {}