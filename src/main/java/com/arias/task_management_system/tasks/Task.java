package com.arias.task_management_system.tasks;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Task(
        @Null
        Long id,
        @NotNull
        Long creatorId,
        @NotNull
        Long assignedUserId,
        TaskStatus status,
        @NotNull
        @FutureOrPresent
        LocalDateTime createDateTime,
        @NotNull
        @Future
        LocalDate deadlineDate,
        LocalDateTime doneDateTime,
        @NotNull
        TaskPriority priority
) {}
