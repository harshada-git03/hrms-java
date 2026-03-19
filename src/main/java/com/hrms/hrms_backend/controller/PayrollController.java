package com.hrms.hrms_backend.controller;

import com.hrms.hrms_backend.model.dto.request.PayrollRequest;
import com.hrms.hrms_backend.model.dto.response.PayrollResponse;
import com.hrms.hrms_backend.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    // Generate payroll — Admin / HR Manager only
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<PayrollResponse> generatePayroll(
            @Valid @RequestBody PayrollRequest request) {
        return ResponseEntity.ok(payrollService.generatePayroll(request));
    }

    // Mark as paid — Admin only
    @PutMapping("/pay/{payrollId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PayrollResponse> markAsPaid(@PathVariable Long payrollId) {
        return ResponseEntity.ok(payrollService.markAsPaid(payrollId));
    }

    // Get payroll for an employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayrollResponse>> getEmployeePayroll(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(payrollService.getPayrollByEmployee(employeeId));
    }

    // Get all payroll for a month — HR view
    @GetMapping("/{month}/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<PayrollResponse>> getPayrollByMonth(
            @PathVariable int month,
            @PathVariable int year) {
        return ResponseEntity.ok(payrollService.getPayrollByMonthYear(month, year));
    }
}
