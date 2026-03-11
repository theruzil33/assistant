package com.theruzil.life_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "task_statuses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tracking_id", "task_id", "date"})
})
@Getter
@Setter
@NoArgsConstructor
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_id", nullable = false)
    private Long trackingId;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false)
    private LocalDate date;
}
