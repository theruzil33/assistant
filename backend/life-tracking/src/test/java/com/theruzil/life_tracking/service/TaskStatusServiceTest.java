package com.theruzil.life_tracking.service;

import com.theruzil.life_tracking.entity.TaskStatus;
import com.theruzil.life_tracking.repository.TaskStatusRepository;
import com.theruzil.life_tracking.repository.TrackingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskStatusServiceTest {

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @Mock
    private TrackingRepository trackingRepository;

    @InjectMocks
    private TaskStatusService taskStatusService;

    private TaskStatus taskStatus;

    @BeforeEach
    void setUp() {
        taskStatus = new TaskStatus();
        taskStatus.setId(1L);
        taskStatus.setTrackingId(10L);
        taskStatus.setTaskId(20L);
        taskStatus.setStatus("DONE");
        taskStatus.setDate(LocalDate.of(2026, 3, 11));
    }

    @Test
    void getByTrackingTask_returnsMatchingStatuses() {
        when(taskStatusRepository.findByTrackingIdAndTaskId(10L, 20L)).thenReturn(List.of(taskStatus));

        List<TaskStatus> result = taskStatusService.getByTrackingTask(10L, 20L);

        assertThat(result).hasSize(1).contains(taskStatus);
    }

    @Test
    void getById_existingId_returnsTaskStatus() {
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(taskStatus));

        TaskStatus result = taskStatusService.getById(1L);

        assertThat(result).isEqualTo(taskStatus);
    }

    @Test
    void getById_notExistingId_throwsException() {
        when(taskStatusRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskStatusService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_validTrackingTask_savesAndReturns() {
        when(trackingRepository.existsTrackingTask(10L, 20L)).thenReturn(true);
        when(taskStatusRepository.save(taskStatus)).thenReturn(taskStatus);

        TaskStatus result = taskStatusService.create(taskStatus);

        assertThat(result).isEqualTo(taskStatus);
        verify(taskStatusRepository).save(taskStatus);
    }

    @Test
    void create_noTrackingTaskConnection_throwsException() {
        when(trackingRepository.existsTrackingTask(10L, 20L)).thenReturn(false);

        assertThatThrownBy(() -> taskStatusService.create(taskStatus))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("10")
                .hasMessageContaining("20");

        verify(taskStatusRepository, never()).save(any());
    }

    @Test
    void update_existingId_updatesStatusAndDate() {
        TaskStatus updated = new TaskStatus();
        updated.setStatus("NOT_DONE");
        updated.setDate(LocalDate.of(2026, 3, 12));

        when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(taskStatus));
        when(taskStatusRepository.save(taskStatus)).thenReturn(taskStatus);

        TaskStatus result = taskStatusService.update(1L, updated);

        assertThat(result.getStatus()).isEqualTo("NOT_DONE");
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2026, 3, 12));
        verify(taskStatusRepository).save(taskStatus);
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        taskStatusService.delete(1L);

        verify(taskStatusRepository).deleteById(1L);
    }
}
