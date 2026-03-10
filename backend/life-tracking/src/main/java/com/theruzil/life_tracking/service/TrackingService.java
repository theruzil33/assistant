package com.theruzil.life_tracking.service;

import com.theruzil.life_tracking.entity.Tracking;
import com.theruzil.life_tracking.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingRepository trackingRepository;

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
}
