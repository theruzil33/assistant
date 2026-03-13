package com.theruzil.auth.controller;

import com.theruzil.auth.dto.LoginRequest;
import com.theruzil.auth.service.LifeTrackingProxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LifeTrackingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    LifeTrackingProxyService lifeTrackingProxyService;

    @Test
    void lifeTracking_withoutSession_returns401() throws Exception {
        mockMvc.perform(get("/life-tracking/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void lifeTracking_afterLogin_proxiesRequest() throws Exception {
        when(lifeTrackingProxyService.forward(any()))
                .thenReturn(ResponseEntity.ok("proxied response".getBytes()));

        var session = loginAsAdmin();

        mockMvc.perform(get("/life-tracking/tasks").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("proxied response"));
    }

    @Test
    void lifeTracking_afterLogin_propagatesDownstreamStatus() throws Exception {
        when(lifeTrackingProxyService.forward(any()))
                .thenReturn(ResponseEntity.status(404).body("not found".getBytes()));

        var session = loginAsAdmin();

        mockMvc.perform(get("/life-tracking/tasks/999").session(session))
                .andExpect(status().isNotFound());
    }

    private MockHttpSession loginAsAdmin() throws Exception {
        var request = new LoginRequest("admin", "password");

        var result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        return (MockHttpSession) result.getRequest().getSession(false);
    }
}
