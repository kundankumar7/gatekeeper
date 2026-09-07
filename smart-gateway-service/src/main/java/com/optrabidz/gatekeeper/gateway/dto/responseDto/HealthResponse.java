package com.optrabidz.gatekeeper.gateway.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class HealthResponse {
    private String serviceName;
    private String serviceDescription;
    private String serviceStatus;
}
