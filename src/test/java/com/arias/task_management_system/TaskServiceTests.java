package com.arias.task_management_system;

import com.arias.task_management_system.tasks.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {

    @Mock
    private TaskRepository repository;

    @Mock
    private TaskMapper mapper;

    @InjectMocks
    private TaskService service;

    @Test
    void shouldNotStartTaskWhenUserAlreadyHasFiveTasksInProgress() {
        Long taskId = 1L;
        Long userId = 10L;

        TaskEntity taskEntity = new TaskEntity(
                taskId,
                2L,
                userId,
                TaskStatus.CREATED,
                LocalDateTime.now(),
                LocalDate.now().plusDays(1),
                null,
                TaskPriority.MEDIUM
        );

        when(repository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(repository.countByAssignedUserIdAndStatus(userId, TaskStatus.IN_PROGRESS)).thenReturn(5L);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.startTask(taskId)
        );

        assertTrue(exception.getMessage().contains("Cannot start more than 5 tasks."));
        assertEquals(TaskStatus.CREATED, taskEntity.getStatus());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldStartTaskWhenUserHasFourTasksInProgress() {
        Long taskId = 1L;
        Long userId = 10L;

        TaskEntity taskEntity = new TaskEntity(
                taskId,
                2L,
                userId,
                TaskStatus.CREATED,
                LocalDateTime.now(),
                LocalDate.now().plusDays(1),
                null,
                TaskPriority.MEDIUM
        );

        when(repository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(repository.countByAssignedUserIdAndStatus(userId, TaskStatus.IN_PROGRESS)).thenReturn(4L);

        service.startTask(taskId);

        assertEquals(TaskStatus.IN_PROGRESS, taskEntity.getStatus());

        verify(repository).save(taskEntity);
    }

    @Test
    void shouldNotCompleteTaskWhenTaskAlreadyComplete() {
        Long taskId = 1L;
        Long userId = 10L;

        TaskEntity taskEntity = new TaskEntity(
                taskId,
                2L,
                userId,
                TaskStatus.DONE,
                LocalDateTime.now(),
                LocalDate.now().plusDays(1),
                null,
                TaskPriority.MEDIUM
        );

        when(repository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.completeTask(taskId)
        );

        assertTrue(exception.getMessage().contains("Cannot complete task: status is DONE"));
        assertEquals(TaskStatus.DONE, taskEntity.getStatus());

        verify(repository, never()).save(any());
    }

    @Test
    void shouldCompleteTaskWhenTaskInProgress() {
        Long taskId = 1L;
        Long userId = 10L;

        TaskEntity taskEntity = new TaskEntity(
                taskId,
                2L,
                userId,
                TaskStatus.IN_PROGRESS,
                LocalDateTime.now(),
                LocalDate.now().plusDays(1),
                null,
                TaskPriority.MEDIUM
        );

        when(repository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        service.completeTask(taskId);

        assertEquals(TaskStatus.DONE, taskEntity.getStatus());
        assertNotNull(taskEntity.getDoneDateTime());
    }

    @Test
    void shouldNotUpdateTaskWhenTaskAlreadyComplete() {
        Long taskId = 1L;
        Long userId = 10L;

        TaskEntity taskEntity = new TaskEntity(
                taskId,
                2L,
                userId,
                TaskStatus.DONE,
                LocalDateTime.now(),
                LocalDate.now().plusDays(1),
                LocalDateTime.now(),
                TaskPriority.MEDIUM
        );

        Task taskToUpdate = new Task(
                taskId,
                2L,
                userId,
                TaskStatus.DONE,
                taskEntity.getCreateDateTime(),
                LocalDate.now().plusDays(2),
                taskEntity.getDoneDateTime(),
                TaskPriority.HIGH
        );

        when(repository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.updateTask(taskId, taskToUpdate)
        );

        assertEquals("Cannot modify task: status is DONE", exception.getMessage());
        assertEquals(TaskPriority.MEDIUM, taskEntity.getPriority());
        assertEquals(TaskStatus.DONE, taskEntity.getStatus());

        verify(repository, never()).save(any());
    }

    @Test
    void shouldRejectNegativePageNumber() {
        TaskSearchFilter filter = new TaskSearchFilter(
                null,
                null,
                null,
                null,
                10,
                -1
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.searchAllByFilter(filter)
        );

        assertEquals("Page number cannot be negative", exception.getMessage());

        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectPageSizeGreaterThan100() {
        TaskSearchFilter filter = new TaskSearchFilter(
                null,
                null,
                null,
                null,
                101,
                0
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.searchAllByFilter(filter)
        );

        assertEquals("Page size should be between 1 and 100", exception.getMessage());

        verifyNoInteractions(repository);
    }
}
