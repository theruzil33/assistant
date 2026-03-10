package com.theruzil.life_tracking.repository;

import com.theruzil.life_tracking.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
