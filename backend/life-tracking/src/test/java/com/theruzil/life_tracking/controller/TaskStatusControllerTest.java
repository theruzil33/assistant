package com.theruzil.life_tracking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.theruzil.life_tracking.config.SecurityConfig;
import com.theruzil.life_tracking.entity.TaskStatus;
import com.theruzil.life_tracking.service.TaskStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskStatusController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
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
    void getByTrackingTask_returns200WithList() throws Exception {
        when(taskStatusService.getByTrackingTask(10L, 20L)).thenReturn(List.of(taskStatus));

        mockMvc.perform(get("/api/task-statuses")
                        .param("trackingId", "10")
                        .param("taskId", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("DONE"))
                .andExpect(jsonPath("$[0].date").value("2026-03-11"));
    }

    @Test
    void getById_existingId_returns200() throws Exception {
        when(taskStatusService.getById(1L)).thenReturn(taskStatus);

        mockMvc.perform(get("/api/task-statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void getById_notExistingId_returns404() throws Exception {
        when(taskStatusService.getById(99L)).thenThrow(new RuntimeException("TaskStatus not found with id: 99"));

        mockMvc.perform(get("/api/task-statuses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validBody_returns201() throws Exception {
        when(taskStatusService.create(any(TaskStatus.class))).thenReturn(taskStatus);

        mockMvc.perform(post("/api/task-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.trackingId").value(10))
                .andExpect(jsonPath("$.taskId").value(20));
    }

    @Test
    void create_noConnection_returns404() throws Exception {
        when(taskStatusService.create(any(TaskStatus.class)))
                .thenThrow(new RuntimeException("No connection between tracking 10 and task 20"));

        mockMvc.perform(post("/api/task-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_existingId_returns200() throws Exception {
        TaskStatus updated = new TaskStatus();
        updated.setStatus("NOT_DONE");
        updated.setDate(LocalDate.of(2026, 3, 12));

        when(taskStatusService.update(eq(1L), any(TaskStatus.class))).thenReturn(updated);

        mockMvc.perform(put("/api/task-statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_DONE"))
                .andExpect(jsonPath("$.date").value("2026-03-12"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        doNothing().when(taskStatusService).delete(1L);

        mockMvc.perform(delete("/api/task-statuses/1"))
                .andExpect(status().isNoContent());

        verify(taskStatusService).delete(1L);
    }
}
