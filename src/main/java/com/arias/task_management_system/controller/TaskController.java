package com.arias.task_management_system.controller;

import com.arias.task_management_system.Task;
import com.arias.task_management_system.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id) {
        log.info("Called method get taskById: {}", id);
        Task task = taskService.getTaskById(id);
        log.info("Ending method get taskById");
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody @Valid Task task) {
        log.info("Called method createTask");
        Task newTask = taskService.createTask(task);
        log.info("Ending method createTask");
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody @Valid Task taskToUpdate) {
        log.info("Called method updateTask");
        Task updatedTask = taskService.updateTask(id, taskToUpdate);
        log.info("Ending method updateTask");
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable("id") Long id) {
        log.info("Called method deleteTask");
        taskService.deleteTask(id);
        log.info("Ending method deleteTask");
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Task> startTask(@PathVariable("id") Long id) {
        log.info("Called method startTask with id {}", id);
        Task task = taskService.startTask(id);
        log.info("Ending method startTask");
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        log.info("Called method completeTask with id {}", id);
        Task task = taskService.completeTask(id);
        log.info("Ending method completeTask");
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }
}
