package com.hrms.hrms_backend.model.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AttendanceResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private String workMode;
    private String status;
}
