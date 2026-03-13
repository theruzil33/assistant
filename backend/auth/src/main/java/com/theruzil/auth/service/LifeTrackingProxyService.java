package com.theruzil.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LifeTrackingProxyService extends ProxyService {

    @Value("${life-tracking.url}")
    private String lifeTrackingUrl;

    @Override
    protected String getBaseUrl() {
        return lifeTrackingUrl;
    }

    @Override
    protected String getPathPrefix() {
        return "/life-tracking";
    }
}
