package com.hrms.hrms_backend.model.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmployeeResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;
}
