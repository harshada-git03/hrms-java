package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.model.dto.request.LeaveActionRequest;
import com.hrms.hrms_backend.model.dto.request.LeaveRequestDto;
import com.hrms.hrms_backend.model.dto.response.LeaveResponse;
import com.hrms.hrms_backend.model.entity.Employee;
import com.hrms.hrms_backend.model.entity.LeaveRequest;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import com.hrms.hrms_backend.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    // Apply for leave
    public LeaveResponse applyLeave(Long employeeId, LeaveRequestDto dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(dto.getLeaveType());
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setTotalDays(calculateWorkingDays(dto.getStartDate(), dto.getEndDate()));
        leave.setReason(dto.getReason());

        return mapToResponse(leaveRequestRepository.save(leave));
    }

    // Approve or reject leave — HR Manager / Admin only
    public LeaveResponse processLeave(Long leaveId, Long approverId,
                                       LeaveActionRequest action) {
        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (!leave.getStatus().equals("PENDING")) {
            throw new RuntimeException("Leave request already processed");
        }

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        leave.setStatus(action.getAction()); // APPROVED or REJECTED
        leave.setApprovedBy(approver);

        if (action.getAction().equals("REJECTED")) {
            leave.setRejectionReason(action.getRejectionReason());
        }

        return mapToResponse(leaveRequestRepository.save(leave));
    }

    // Get all leaves for an employee
    public List<LeaveResponse> getLeavesByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get all pending leaves — for HR dashboard
    public List<LeaveResponse> getPendingLeaves() {
        return leaveRequestRepository.findByStatus("PENDING")
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Calculate working days (excludes weekends)
    private int calculateWorkingDays(LocalDate start, LocalDate end) {
        int workingDays = 0;
        LocalDate current = start;
        while (!current.isAfter(end)) {
            DayOfWeek day = current.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            current = current.plusDays(1);
        }
        return workingDays;
    }

    private LeaveResponse mapToResponse(LeaveRequest leave) {
        LeaveResponse response = new LeaveResponse();
        response.setId(leave.getId());
        response.setEmployeeId(leave.getEmployee().getId());
        response.setEmployeeName(leave.getEmployee().getFullName());
        response.setLeaveType(leave.getLeaveType());
        response.setStartDate(leave.getStartDate());
        response.setEndDate(leave.getEndDate());
        response.setTotalDays(leave.getTotalDays());
        response.setReason(leave.getReason());
        response.setStatus(leave.getStatus());
        response.setAppliedAt(leave.getAppliedAt());
        response.setRejectionReason(leave.getRejectionReason());
        if (leave.getApprovedBy() != null) {
            response.setApprovedByName(leave.getApprovedBy().getFullName());
        }
        return response;
    }
}
