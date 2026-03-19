package com.hrms.hrms_backend.model.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HolidayResponse {
    private Long id;
    private String name;
    private LocalDate date;
    private String type;
    private String description;
}
