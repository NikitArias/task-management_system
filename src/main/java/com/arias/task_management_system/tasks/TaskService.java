package com.arias.task_management_system.tasks;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository taskRepository, TaskMapper mapper) {
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    public Task getTaskById(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task with id = " + id));
        return mapper.toDomain(taskEntity);
    }

    public List<Task> getAllTasks() {
        List<TaskEntity> taskEntities = taskRepository.findAll();
        return taskEntities.stream().map(mapper::toDomain).toList();
    }

    public Task createTask(Task task) {
        if (task.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        if (!task.deadlineDate().isAfter(task.createDateTime().toLocalDate())) {
            throw new IllegalArgumentException("Deadline date should be after create date");
        }
        TaskEntity newTask = mapper.toEntity(task);
        newTask.setStatus(TaskStatus.CREATED);

        TaskEntity savedTask = taskRepository.save(newTask);
        return mapper.toDomain(savedTask);
    }

    public Task updateTask(Long id, Task taskToUpdate) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task with id = " + id));
        if (task.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Cannot modify task: status is " + task.getStatus());
        }
        if (!taskToUpdate.deadlineDate().isAfter(taskToUpdate.createDateTime().toLocalDate())) {
            throw new IllegalArgumentException("Deadline date should be after create date");
        }
        TaskEntity updatedTask = mapper.toEntity(taskToUpdate);
        updatedTask.setId(task.getId());
        updatedTask.setStatus(task.getStatus());

        TaskEntity savedTask = taskRepository.save(updatedTask);
        return mapper.toDomain(savedTask);
    }

    public void deleteTask(Long id) {
        TaskEntity taskForDelete = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task with id = " + id));
        taskRepository.delete(taskForDelete);
    }

    @Transactional
    public Task startTask(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task with id = " + id));
        if (taskEntity.getStatus() != TaskStatus.CREATED) {
            throw new IllegalStateException("Cannot start task: status is " + taskEntity.getStatus());
        }
        Long countInProgress = taskRepository
                .countByAssignedUserIdAndStatus(taskEntity.getAssignedUserId(), TaskStatus.IN_PROGRESS);
        if (countInProgress >= 5) {
            throw new IllegalStateException("Cannot start more than 4 tasks. Task count is " + countInProgress);
        }
        taskEntity.setStatus(TaskStatus.IN_PROGRESS);
        taskEntity.setDoneDateTime(null);
        taskRepository.save(taskEntity);
        return mapper.toDomain(taskEntity);
    }

    @Transactional
    public Task completeTask(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task with id = " + id));
        if (taskEntity.getAssignedUserId() == null) {
            throw new IllegalArgumentException("Cannot complete task: assigned user id is null");
        }
        if (taskEntity.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete task: status is " + taskEntity.getStatus());
        }
        taskEntity.setDoneDateTime(LocalDateTime.now());
        taskEntity.setStatus(TaskStatus.DONE);
        return mapper.toDomain(taskEntity);
    }

    public List<Task> searchAllByFilter(TaskSearchFilter filter) {
        int pageSize = filter.pageSize() != null ? filter.pageSize() : 10;
        int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;
        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("Page size should be between 1 and 100");
        }
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<TaskEntity> allTasks = taskRepository.searchAllByFilter(
                filter.creatorId(), filter.assignedUserId(), filter.status(), filter.priority(), pageable
                );
        return allTasks.stream().map(mapper::toDomain).toList();
    }
}
