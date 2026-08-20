package com.arias.task_management_system.service;

import com.arias.task_management_system.Priority;
import com.arias.task_management_system.Status;
import com.arias.task_management_system.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class TaskService {
    private final Map<Long, Task> tasksMap = Map.of(
            1L, new Task(
                    1L,
                    1L,
                    1L,
                    Status.CREATED,
                    LocalDateTime.now(),
                    LocalDate.now().plusDays(2),
                    Priority.MEDIUM),
            2L, new Task(
                    2L,
                    2L,
                    2L,
                    Status.IN_PROGRESS,
                    LocalDateTime.now(),
                    LocalDate.now().plusDays(1),
                    Priority.HIGH),
            3L, new Task(
                    3L,
                    3L,
                    3L,
                    Status.IN_PROGRESS,
                    LocalDateTime.now(),
                    LocalDate.now().plusDays(5),
                    Priority.LOW)
    );

    public Task getTaskById(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new NoSuchElementException("Task with id " + id + " not found");
        }
        return tasksMap.get(id);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasksMap.values());
    }
}
