package com.hrms.hrms_backend.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayrollRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Month is required")
    private int month;

    @NotNull(message = "Year is required")
    private int year;

    private double basicSalary;
    private double hra;
    private double allowances;
    private double deductions;
}
