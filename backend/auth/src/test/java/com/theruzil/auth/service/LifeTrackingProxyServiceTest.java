package com.theruzil.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class LifeTrackingProxyServiceTest {

    private LifeTrackingProxyService service;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        service = new LifeTrackingProxyService();
        ReflectionTestUtils.setField(service, "lifeTrackingUrl", "http://life-tracking-service");

        var restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
    }

    @Test
    void forward_get_proxiesPathAndReturnsResponse() {
        mockServer.expect(requestTo("http://life-tracking-service/tasks"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", org.springframework.http.MediaType.APPLICATION_JSON));

        var request = new MockHttpServletRequest("GET", "/life-tracking/tasks");
        var response = service.forward(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(new String(response.getBody())).isEqualTo("[]");
        mockServer.verify();
    }

    @Test
    void forward_withQueryParams_appendsQueryString() {
        mockServer.expect(requestTo("http://life-tracking-service/tasks?page=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", org.springframework.http.MediaType.APPLICATION_JSON));

        var request = new MockHttpServletRequest("GET", "/life-tracking/tasks");
        request.setQueryString("page=1");

        var response = service.forward(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void forward_downstreamReturns404_propagatesStatus() {
        mockServer.expect(requestTo("http://life-tracking-service/tasks/999"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        var request = new MockHttpServletRequest("GET", "/life-tracking/tasks/999");
        var response = service.forward(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        mockServer.verify();
    }
}
