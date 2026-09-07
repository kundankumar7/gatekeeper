package com.optrabidz.gatekeeper.analytics.controller;

import com.optrabidz.gatekeeper.analytics.dto.responseDto.HealthResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    private String serviceName;
    private String serviceDescription;
    private String serviceStatus;

    public HealthController(@Value("${info.app.name}") String serviceName,
                            @Value("${info.app.description}") String serviceDescription) {
        this.serviceName = "Analytics Service";
        this.serviceDescription = "Analytics Service";
    }

    @GetMapping
    public ResponseEntity<HealthResponse> getHealth() {
        return ResponseEntity.ok(
                new HealthResponse(serviceName, serviceDescription, "UP"));
    }
}
