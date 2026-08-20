package com.arias.task_management_system.controller;

import com.arias.task_management_system.Task;
import com.arias.task_management_system.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks() {
        log.info("Called method get all tasks");
        List<Task> tasks = taskService.getAllTasks();
        log.info("Ending method get all tasks");
        return tasks;
    }

    @GetMapping("/tasks/{id}")
    public Task getTaskById(@PathVariable Long id) {
        log.info("Called method get task by id: {}", id);
        Task task = taskService.getTaskById(id);
        log.info("Ending method get task by id");
        return task;
    }
}
