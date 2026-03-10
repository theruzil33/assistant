package com.theruzil.life_tracking.service;

import com.theruzil.life_tracking.entity.Task;
import com.theruzil.life_tracking.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Пробежка");
        task.setActive(true);
        task.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAll_returnsAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> result = taskService.getAll();

        assertThat(result).hasSize(1).contains(task);
    }

    @Test
    void getById_existingId_returnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getById(1L);

        assertThat(result).isEqualTo(task);
    }

    @Test
    void getById_notExistingId_throwsException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsTask() {
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.create(task);

        assertThat(result).isEqualTo(task);
        verify(taskRepository).save(task);
    }

    @Test
    void update_existingId_updatesFields() {
        Task updated = new Task();
        updated.setTitle("Медитация");
        updated.setActive(false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.update(1L, updated);

        assertThat(result.getTitle()).isEqualTo("Медитация");
        assertThat(result.isActive()).isFalse();
        verify(taskRepository).save(task);
    }

    @Test
    void delete_callsRepositoryDeleteById() {
        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }
}
