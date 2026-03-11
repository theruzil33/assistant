package com.theruzil.life_tracking.controller;

import com.theruzil.life_tracking.entity.TaskStatus;
import com.theruzil.life_tracking.service.TaskStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-statuses")
@RequiredArgsConstructor
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    @GetMapping
    public List<TaskStatus> getByTrackingTask(
            @RequestParam Long trackingId,
            @RequestParam Long taskId) {
        return taskStatusService.getByTrackingTask(trackingId, taskId);
    }

    @GetMapping("/{id}")
    public TaskStatus getById(@PathVariable Long id) {
        return taskStatusService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus create(@RequestBody TaskStatus taskStatus) {
        return taskStatusService.create(taskStatus);
    }

    @PutMapping("/{id}")
    public TaskStatus update(@PathVariable Long id, @RequestBody TaskStatus taskStatus) {
        return taskStatusService.update(id, taskStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusService.delete(id);
    }
}
