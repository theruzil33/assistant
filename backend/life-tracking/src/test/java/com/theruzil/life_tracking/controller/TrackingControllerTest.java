package com.theruzil.life_tracking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.theruzil.life_tracking.config.SecurityConfig;
import com.theruzil.life_tracking.entity.Task;
import com.theruzil.life_tracking.entity.Tracking;
import com.theruzil.life_tracking.service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrackingController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class TrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private TrackingService trackingService;

    private Tracking tracking;
    private Task task;

    @BeforeEach
    void setUp() {
        tracking = new Tracking();
        tracking.setId(1L);
        tracking.setTitle("Марафон");
        tracking.setStartDate(LocalDate.of(2026, 4, 1));
        tracking.setEndDate(LocalDate.of(2026, 6, 30));
        tracking.setCreatedAt(LocalDateTime.now());

        task = new Task();
        task.setId(10L);
        task.setTitle("Ежедневная пробежка");
        task.setActive(true);
        task.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAll_returns200WithTrackingList() throws Exception {
        when(trackingService.getAll()).thenReturn(List.of(tracking));

        mockMvc.perform(get("/api/trackings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Марафон"))
                .andExpect(jsonPath("$[0].startDate").value("2026-04-01"))
                .andExpect(jsonPath("$[0].endDate").value("2026-06-30"));
    }

    @Test
    void getById_existingId_returns200WithTracking() throws Exception {
        when(trackingService.getById(1L)).thenReturn(tracking);

        mockMvc.perform(get("/api/trackings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Марафон"));
    }

    @Test
    void getById_notExistingId_returns404() throws Exception {
        when(trackingService.getById(99L)).thenThrow(new RuntimeException("Tracking not found with id: 99"));

        mockMvc.perform(get("/api/trackings/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_withDates_returns201WithTracking() throws Exception {
        when(trackingService.create(any(Tracking.class))).thenReturn(tracking);

        mockMvc.perform(post("/api/trackings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tracking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Марафон"))
                .andExpect(jsonPath("$.startDate").value("2026-04-01"))
                .andExpect(jsonPath("$.endDate").value("2026-06-30"));
    }

    @Test
    void create_withoutDates_returns201WithTracking() throws Exception {
        Tracking withoutDates = new Tracking();
        withoutDates.setId(2L);
        withoutDates.setTitle("Бег");
        withoutDates.setCreatedAt(LocalDateTime.now());

        when(trackingService.create(any(Tracking.class))).thenReturn(withoutDates);

        mockMvc.perform(post("/api/trackings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Бег\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Бег"))
                .andExpect(jsonPath("$.startDate").doesNotExist())
                .andExpect(jsonPath("$.endDate").doesNotExist());
    }

    @Test
    void update_existingId_returns200WithUpdatedTracking() throws Exception {
        Tracking updated = new Tracking();
        updated.setTitle("Полумарафон");
        updated.setStartDate(LocalDate.of(2026, 5, 1));
        updated.setEndDate(null);

        when(trackingService.update(eq(1L), any(Tracking.class))).thenReturn(updated);

        mockMvc.perform(put("/api/trackings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Полумарафон"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        doNothing().when(trackingService).delete(1L);

        mockMvc.perform(delete("/api/trackings/1"))
                .andExpect(status().isNoContent());

        verify(trackingService).delete(1L);
    }

    @Test
    void getTasks_returns200WithTaskSet() throws Exception {
        when(trackingService.getTasks(1L)).thenReturn(Set.of(task));

        mockMvc.perform(get("/api/trackings/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].title").value("Ежедневная пробежка"));
    }

    @Test
    void addTask_returns200WithUpdatedTracking() throws Exception {
        tracking.getTasks().add(task);
        when(trackingService.addTask(1L, 10L)).thenReturn(tracking);

        mockMvc.perform(post("/api/trackings/1/tasks/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void removeTask_returns204() throws Exception {
        doNothing().when(trackingService).removeTask(1L, 10L);

        mockMvc.perform(delete("/api/trackings/1/tasks/10"))
                .andExpect(status().isNoContent());

        verify(trackingService).removeTask(1L, 10L);
    }
}
