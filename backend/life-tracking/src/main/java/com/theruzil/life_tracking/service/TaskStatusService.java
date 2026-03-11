package com.theruzil.life_tracking.service;

import com.theruzil.life_tracking.entity.TaskStatus;
import com.theruzil.life_tracking.repository.TaskStatusRepository;
import com.theruzil.life_tracking.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TrackingRepository trackingRepository;

    public List<TaskStatus> getByTrackingTask(Long trackingId, Long taskId) {
        return taskStatusRepository.findByTrackingIdAndTaskId(trackingId, taskId);
    }

    public TaskStatus getById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found with id: " + id));
    }

    public TaskStatus create(TaskStatus taskStatus) {
        if (!trackingRepository.existsTrackingTask(taskStatus.getTrackingId(), taskStatus.getTaskId())) {
            throw new RuntimeException("No connection between tracking " + taskStatus.getTrackingId() + " and task " + taskStatus.getTaskId());
        }
        return taskStatusRepository.save(taskStatus);
    }

    public TaskStatus update(Long id, TaskStatus updated) {
        TaskStatus taskStatus = getById(id);
        taskStatus.setStatus(updated.getStatus());
        taskStatus.setDate(updated.getDate());
        return taskStatusRepository.save(taskStatus);
    }

    public void delete(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
