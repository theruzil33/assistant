package com.theruzil.life_tracking.repository;

import com.theruzil.life_tracking.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    List<TaskStatus> findByTrackingIdAndTaskId(Long trackingId, Long taskId);
}
