package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.model.dto.request.PayrollRequest;
import com.hrms.hrms_backend.model.dto.response.PayrollResponse;
import com.hrms.hrms_backend.model.entity.Employee;
import com.hrms.hrms_backend.model.entity.Payroll;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import com.hrms.hrms_backend.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    // Generate payroll for an employee
    public PayrollResponse generatePayroll(PayrollRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Check if payroll already generated for this month
        payrollRepository.findByEmployeeIdAndMonthAndYear(
                request.getEmployeeId(), request.getMonth(), request.getYear())
                .ifPresent(p -> {
                    throw new RuntimeException("Payroll already generated for this month");
                });

        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);
        payroll.setMonth(request.getMonth());
        payroll.setYear(request.getYear());
        payroll.setBasicSalary(request.getBasicSalary());
        payroll.setHra(request.getHra());
        payroll.setAllowances(request.getAllowances());
        payroll.setDeductions(request.getDeductions());
        payroll.setNetSalary(
            request.getBasicSalary() + request.getHra() +
            request.getAllowances() - request.getDeductions()
        );

        return mapToResponse(payrollRepository.save(payroll));
    }

    // Mark payroll as paid
    public PayrollResponse markAsPaid(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll record not found"));

        payroll.setStatus("PAID");
        payroll.setPaidOn(LocalDate.now());
        return mapToResponse(payrollRepository.save(payroll));
    }

    // Get all payroll for an employee
    public List<PayrollResponse> getPayrollByEmployee(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get all payroll for a specific month — HR view
    public List<PayrollResponse> getPayrollByMonthYear(int month, int year) {
        return payrollRepository.findByMonthAndYear(month, year)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PayrollResponse mapToResponse(Payroll payroll) {
        PayrollResponse response = new PayrollResponse();
        response.setId(payroll.getId());
        response.setEmployeeId(payroll.getEmployee().getId());
        response.setEmployeeName(payroll.getEmployee().getFullName());
        response.setMonth(payroll.getMonth());
        response.setYear(payroll.getYear());
        response.setBasicSalary(payroll.getBasicSalary());
        response.setHra(payroll.getHra());
        response.setAllowances(payroll.getAllowances());
        response.setDeductions(payroll.getDeductions());
        response.setNetSalary(payroll.getNetSalary());
        response.setStatus(payroll.getStatus());
        response.setGeneratedAt(payroll.getGeneratedAt());
        response.setPaidOn(payroll.getPaidOn());
        return response;
    }
}
