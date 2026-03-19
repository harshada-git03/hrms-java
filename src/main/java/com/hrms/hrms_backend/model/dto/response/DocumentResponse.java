package com.hrms.hrms_backend.model.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String title;
    private String type;
    private String status;
    private String requestedByName;
    private String fileUrl;
    private String remarks;
    private LocalDateTime requestedAt;
    private LocalDateTime signedAt;
}
