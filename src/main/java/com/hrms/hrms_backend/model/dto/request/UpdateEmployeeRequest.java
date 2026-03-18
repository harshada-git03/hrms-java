package com.hrms.hrms_backend.model.dto.request;

import lombok.Data;

@Data
public class UpdateEmployeeRequest {
    private String fullName;
    private String role;
    private String status;
}
