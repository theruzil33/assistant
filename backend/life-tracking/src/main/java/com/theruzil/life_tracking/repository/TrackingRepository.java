package com.theruzil.life_tracking.repository;

import com.theruzil.life_tracking.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {
}
