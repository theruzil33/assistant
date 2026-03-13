package com.theruzil.auth.controller;

import com.theruzil.auth.service.LifeTrackingProxyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/life-tracking")
@RequiredArgsConstructor
public class LifeTrackingController {

    private final LifeTrackingProxyService lifeTrackingProxyService;

    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) {
        return lifeTrackingProxyService.forward(request);
    }
}
