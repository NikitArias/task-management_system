package com.arias.task_management_system.service;

import com.arias.task_management_system.Status;
import com.arias.task_management_system.Task;
import com.arias.task_management_system.entity.TaskEntity;
import com.arias.task_management_system.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
//    private final Map<Long, Task> tasksMap;
//    private final AtomicLong idCounter;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
//        this.tasksMap = new HashMap<>();
//        this.idCounter = new AtomicLong();
    }

    public Task getTaskById(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task with id = " + id));
        return toDomainTask(taskEntity);
    }

    public List<Task> getAllTasks() {
        List<TaskEntity> taskEntities = taskRepository.findAll();
        return taskEntities.stream().map(this::toDomainTask).toList();
    }

    public Task createTask(Task task) {
        if (task.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (task.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        TaskEntity newTask = new TaskEntity(
                null,
                task.creatorId(),
                task.assignedUserId(),
                Status.CREATED,
                task.createDateTime(),
                task.deadlineDate(),
                task.priority()
        );
        TaskEntity savedTask = taskRepository.save(newTask);
        return toDomainTask(savedTask);
    }

    public Task updateTask(Long id, Task taskToUpdate) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task with id = " + id));
        if (task.getStatus() == Status.DONE) {
            throw new IllegalStateException("Cannot modify task: status is " + task.getStatus());
        }
        TaskEntity updatedTask = new TaskEntity(
                task.getId(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                task.getStatus(),
                taskToUpdate.createDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
        );
        TaskEntity savedTask = taskRepository.save(updatedTask);
        return toDomainTask(savedTask);
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
        if (taskEntity.getAssignedUserId() == null) {
            throw new IllegalStateException("Cannot start task: assigned user id is null");
        }
        if (taskEntity.getStatus() != Status.CREATED) {
            throw new IllegalStateException("Cannot start task: status is " + taskEntity.getStatus());
        }
        Long countInProgress = taskRepository
                .countByAssignedUserIdAndStatus(taskEntity.getAssignedUserId(), Status.IN_PROGRESS);
        if (countInProgress >= 4) {
            throw new IllegalStateException("Cannot start more than 4 tasks. Task count is " + countInProgress);
        }
        taskEntity.setStatus(Status.IN_PROGRESS);
        taskRepository.save(taskEntity);
        return toDomainTask(taskEntity);
    }

    private Task toDomainTask(TaskEntity taskEntity) {
        return new Task(
                taskEntity.getId(),
                taskEntity.getCreatorId(),
                taskEntity.getAssignedUserId(),
                taskEntity.getStatus(),
                taskEntity.getCreateDateTime(),
                taskEntity.getDeadlineDate(),
                taskEntity.getPriority()
        );
    }
}
