package com.arias.task_management_system.tasks;

public record TaskSearchFilter(
        Long creatorId,
        Long assignedUserId,
        TaskStatus status,
        TaskPriority priority,
        Integer pageSize,
        Integer pageNumber
) {
}
