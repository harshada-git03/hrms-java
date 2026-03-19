package com.hrms.hrms_backend.controller;

import com.hrms.hrms_backend.model.dto.request.LeaveActionRequest;
import com.hrms.hrms_backend.model.dto.request.LeaveRequestDto;
import com.hrms.hrms_backend.model.dto.response.LeaveResponse;
import com.hrms.hrms_backend.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // Apply for leave — any employee
    @PostMapping("/apply/{employeeId}")
    public ResponseEntity<LeaveResponse> applyLeave(
            @PathVariable Long employeeId,
            @Valid @RequestBody LeaveRequestDto request) {
        return ResponseEntity.ok(leaveService.applyLeave(employeeId, request));
    }

    // Approve or reject — HR Manager / Admin only
    @PutMapping("/process/{leaveId}/{approverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<LeaveResponse> processLeave(
            @PathVariable Long leaveId,
            @PathVariable Long approverId,
            @RequestBody LeaveActionRequest action) {
        return ResponseEntity.ok(leaveService.processLeave(leaveId, approverId, action));
    }

    // Get all leaves for an employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveResponse>> getEmployeeLeaves(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getLeavesByEmployee(employeeId));
    }

    // Get all pending leaves — HR dashboard
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<LeaveResponse>> getPendingLeaves() {
        return ResponseEntity.ok(leaveService.getPendingLeaves());
    }
}
