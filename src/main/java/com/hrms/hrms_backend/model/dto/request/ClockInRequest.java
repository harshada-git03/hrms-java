package com.hrms.hrms_backend.model.dto.request;

import lombok.Data;

@Data
public class ClockInRequest {
    private String workMode; // WFH, OFFICE, ON_DUTY
}
