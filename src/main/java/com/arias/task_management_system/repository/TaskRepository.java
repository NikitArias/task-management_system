package com.arias.task_management_system.repository;

import com.arias.task_management_system.Status;
import com.arias.task_management_system.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Long countByAssignedUserIdAndStatus(Long assignedUserId, Status status);
}
