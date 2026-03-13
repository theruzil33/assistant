package com.theruzil.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public abstract class ProxyService {

    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            "transfer-encoding", "connection", "keep-alive",
            "proxy-authenticate", "proxy-authorization", "te", "trailer", "upgrade"
    );

    private RestTemplate restTemplate = new RestTemplate();

    protected abstract String getBaseUrl();

    protected abstract String getPathPrefix();

    public ResponseEntity<byte[]> forward(HttpServletRequest request) {
        String path = request.getRequestURI().replaceFirst(getPathPrefix(), "");
        String query = request.getQueryString();
        String targetUrl = getBaseUrl() + path + (query != null ? "?" + query : "");

        HttpHeaders headers = buildHeaders(request);
        HttpEntity<byte[]> entity = buildEntity(request, headers);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        try {
            ResponseEntity<byte[]> downstream = restTemplate.exchange(URI.create(targetUrl), method, entity, byte[].class);
            HttpHeaders responseHeaders = filterHopByHopHeaders(downstream.getHeaders());
            return ResponseEntity.status(downstream.getStatusCode()).headers(responseHeaders).body(downstream.getBody());
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }

    private HttpHeaders filterHopByHopHeaders(HttpHeaders headers) {
        HttpHeaders filtered = new HttpHeaders();
        headers.forEach((name, values) -> {
            if (!HOP_BY_HOP_HEADERS.contains(name.toLowerCase())) {
                filtered.put(name, values);
            }
        });
        return filtered;
    }

    private HttpHeaders buildHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames()).forEach(name -> {
            if (!name.equalsIgnoreCase("host") && !name.equalsIgnoreCase("cookie")) {
                headers.set(name, request.getHeader(name));
            }
        });
        return headers;
    }

    private HttpEntity<byte[]> buildEntity(HttpServletRequest request, HttpHeaders headers) {
        try {
            byte[] body = request.getInputStream().readAllBytes();
            return body.length > 0 ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);
        } catch (Exception e) {
            return new HttpEntity<>(headers);
        }
    }
}
