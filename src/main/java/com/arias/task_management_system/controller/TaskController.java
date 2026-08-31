package com.arias.task_management_system.controller;

import com.arias.task_management_system.Task;
import com.arias.task_management_system.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping()
    public ResponseEntity<List<Task>> getAllTasks() {
        log.info("Called method getAllTasks");
        List<Task> tasks = taskService.getAllTasks();
        log.info("Ending method getAllTasks");
        return ResponseEntity.status(200).body(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        log.info("Called method get taskById: {}", id);
        try {
            Task task = taskService.getTaskById(id);
            log.info("Ending method get taskById");
            return ResponseEntity.status(HttpStatus.OK).body(task);
        } catch (NoSuchElementException e) {
            log.info("No task found with id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        log.info("Called method createTask");
        Task newTask = taskService.createTask(task);
        log.info("Ending method createTask");
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskToUpdate) {
        log.info("Called method updateTask");
        try {
            Task updatedTask = taskService.updateTask(id, taskToUpdate);
            log.info("Ending method updateTask");
            return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
        } catch (NoSuchElementException e) {
            log.info("No task found with id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable Long id) {
        log.info("Called method deleteTask");
        try {
            taskService.deleteTask(id);
            log.info("Ending method deleteTask");
            return ResponseEntity.status(200).build();
        } catch (NoSuchElementException e) {
            log.info("No task found with id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Task> startTask(@PathVariable Long id) {
        log.info("Called method startTask with id {}", id);
        try {
            Task task = taskService.startTask(id);
            log.info("Ending method startTask");
            return ResponseEntity.status(HttpStatus.OK).body(task);
        } catch (NoSuchElementException e) {
            log.info("No task found with id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
