package com.arias.task_management_system.service;

import com.arias.task_management_system.Status;
import com.arias.task_management_system.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final Map<Long, Task> tasksMap;

    private final AtomicLong idCounter;

    public TaskService() {
        this.tasksMap = new HashMap<>();
        this.idCounter = new AtomicLong();
    }

    public Task getTaskById(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new NoSuchElementException("Task with id " + id + " not found");
        }
        return tasksMap.get(id);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    public Task createTask(Task task) {
        if (task.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (task.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        Task newTask = new Task(
                idCounter.incrementAndGet(),
                task.creatorId(),
                task.assignedUserId(),
                Status.CREATED,
                task.createDateTime(),
                task.deadlineDate(),
                task.priority()
        );
        tasksMap.put(newTask.id(), newTask);
        return newTask;
    }

    public Task updateTask(Long id, Task taskToUpdate) {
        if (!tasksMap.containsKey(id)) {
            throw new NoSuchElementException("Task with id " + id + " not found");
        }
        Task task = tasksMap.get(id);
        if (task.status() == Status.DONE) {
            throw new IllegalStateException("Cannot modify task: status is " + task.status());
        }
        Task updatedTask = new Task(
                task.id(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                task.status(),
                taskToUpdate.createDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
        );
        tasksMap.put(id, updatedTask);
        return updatedTask;
    }

    public void deleteTask(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new NoSuchElementException("Task with id " + id + " not found");
        }
        tasksMap.remove(id);
    }

    public Task rollbackTask(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new NoSuchElementException("Task with id " + id + " not found");
        }
        Task task = tasksMap.get(id);
        if (task.status() != Status.DONE) {
            throw new IllegalStateException("Cannot rollback task: status is " + task.status());
        }
        Task rollbackTask = new Task(
          task.id(),
          task.creatorId(),
          task.assignedUserId(),
          Status.IN_PROGRESS,
          task.createDateTime(),
          task.deadlineDate(),
          task.priority()
        );
        tasksMap.put(id, rollbackTask);

        return rollbackTask;
    }

    public Task changeStatus(Long id) {
        if (!tasksMap.containsKey(id)) {
            throw new NoSuchElementException("Task with id " + id + " not found");
        }
        Task task = tasksMap.get(id);
        if (task.status() == Status.DONE) {
            throw new IllegalStateException("Cannot change status of task: status is " + task.status());
        }
        if (task.status() == Status.IN_PROGRESS) {
            Task updateStatusTask = new Task(
                    task.id(),
                    task.creatorId(),
                    task.assignedUserId(),
                    Status.DONE,
                    task.createDateTime(),
                    task.deadlineDate(),
                    task.priority()
            );
            tasksMap.put(id, updateStatusTask);
            return updateStatusTask;
        } else if (task.status() == Status.CREATED) {
            Task updateStatusTask = new Task(
                    task.id(),
                    task.creatorId(),
                    task.assignedUserId(),
                    Status.IN_PROGRESS,
                    task.createDateTime(),
                    task.deadlineDate(),
                    task.priority()
            );
            tasksMap.put(id, updateStatusTask);
            return updateStatusTask;
        }
        return null;
    }
}
