package com.hrms.hrms_backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotBlank(message = "Title is required")
    private String title;

    private String type = "OTHER";
    private String fileUrl;
    private String remarks;
}
