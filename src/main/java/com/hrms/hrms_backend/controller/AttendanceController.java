package com.hrms.hrms_backend.controller;

import com.hrms.hrms_backend.model.dto.request.ClockInRequest;
import com.hrms.hrms_backend.model.dto.response.AttendanceResponse;
import com.hrms.hrms_backend.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // Clock In
    @PostMapping("/clock-in/{employeeId}")
    public ResponseEntity<AttendanceResponse> clockIn(
            @PathVariable Long employeeId,
            @RequestBody ClockInRequest request) {
        return ResponseEntity.ok(attendanceService.clockIn(employeeId, request));
    }

    // Clock Out
    @PostMapping("/clock-out/{employeeId}")
    public ResponseEntity<AttendanceResponse> clockOut(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.clockOut(employeeId));
    }

    // Get all attendance for an employee
    @GetMapping("/{employeeId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(
                attendanceService.getAttendanceByEmployee(employeeId));
    }
}
