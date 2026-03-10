package com.theruzil.life_tracking.service;

import com.theruzil.life_tracking.entity.Task;
import com.theruzil.life_tracking.entity.Tracking;
import com.theruzil.life_tracking.repository.TaskRepository;
import com.theruzil.life_tracking.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingRepository trackingRepository;
    private final TaskRepository taskRepository;

    public List<Tracking> getAll() {
        return trackingRepository.findAll();
    }

    public Tracking getById(Long id) {
        return trackingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tracking not found with id: " + id));
    }

    public Tracking create(Tracking tracking) {
        return trackingRepository.save(tracking);
    }

    public Tracking update(Long id, Tracking updated) {
        Tracking tracking = getById(id);
        tracking.setTitle(updated.getTitle());
        tracking.setStartDate(updated.getStartDate());
        tracking.setEndDate(updated.getEndDate());
        return trackingRepository.save(tracking);
    }

    public void delete(Long id) {
        trackingRepository.deleteById(id);
    }

    public Set<Task> getTasks(Long trackingId) {
        return getById(trackingId).getTasks();
    }

    public Tracking addTask(Long trackingId, Long taskId) {
        Tracking tracking = getById(trackingId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        tracking.getTasks().add(task);
        return trackingRepository.save(tracking);
    }

    public void removeTask(Long trackingId, Long taskId) {
        Tracking tracking = getById(trackingId);
        tracking.getTasks().removeIf(t -> t.getId().equals(taskId));
        trackingRepository.save(tracking);
    }
}
