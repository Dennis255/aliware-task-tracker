package com.aliware.tasktracker.dto;

import com.aliware.tasktracker.model.TaskStatus;
import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String title,
    String description,
    TaskStatus status,
    LocalDateTime createdAt
) {}