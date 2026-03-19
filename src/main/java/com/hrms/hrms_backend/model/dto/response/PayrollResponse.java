package com.hrms.hrms_backend.model.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PayrollResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private int month;
    private int year;
    private double basicSalary;
    private double hra;
    private double allowances;
    private double deductions;
    private double netSalary;
    private String status;
    private LocalDateTime generatedAt;
    private LocalDate paidOn;
}
