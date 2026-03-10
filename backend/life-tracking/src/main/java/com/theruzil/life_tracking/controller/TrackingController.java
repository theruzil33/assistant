package com.theruzil.life_tracking.controller;

import com.theruzil.life_tracking.entity.Task;
import com.theruzil.life_tracking.entity.Tracking;
import com.theruzil.life_tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/trackings")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping
    public List<Tracking> getAll() {
        return trackingService.getAll();
    }

    @GetMapping("/{id}")
    public Tracking getById(@PathVariable Long id) {
        return trackingService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tracking create(@RequestBody Tracking tracking) {
        return trackingService.create(tracking);
    }

    @PutMapping("/{id}")
    public Tracking update(@PathVariable Long id, @RequestBody Tracking tracking) {
        return trackingService.update(id, tracking);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        trackingService.delete(id);
    }

    @GetMapping("/{id}/tasks")
    public Set<Task> getTasks(@PathVariable Long id) {
        return trackingService.getTasks(id);
    }

    @PostMapping("/{id}/tasks/{taskId}")
    public Tracking addTask(@PathVariable Long id, @PathVariable Long taskId) {
        return trackingService.addTask(id, taskId);
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTask(@PathVariable Long id, @PathVariable Long taskId) {
        trackingService.removeTask(id, taskId);
    }
}
