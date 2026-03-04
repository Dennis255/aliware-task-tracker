package com.aliware.tasktracker.dto;

import java.util.List;

public record PaginatedResponse<T>(List<T> items, long total) {}