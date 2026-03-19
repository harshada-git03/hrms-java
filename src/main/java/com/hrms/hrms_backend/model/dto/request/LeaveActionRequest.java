package com.hrms.hrms_backend.model.dto.request;

import lombok.Data;

@Data
public class LeaveActionRequest {
    private String action; // APPROVED or REJECTED
    private String rejectionReason;
}
