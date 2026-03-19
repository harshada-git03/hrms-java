package com.hrms.hrms_backend.model.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String body;
    private String createdByName;
    private String targetAudience;
    private LocalDateTime createdAt;
}
