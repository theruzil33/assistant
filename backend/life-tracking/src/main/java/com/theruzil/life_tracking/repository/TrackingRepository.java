package com.theruzil.life_tracking.repository;

import com.theruzil.life_tracking.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {

    @Query(value = "SELECT COUNT(*) > 0 FROM tracking_tasks WHERE tracking_id = :trackingId AND task_id = :taskId", nativeQuery = true)
    boolean existsTrackingTask(@Param("trackingId") Long trackingId, @Param("taskId") Long taskId);
}
