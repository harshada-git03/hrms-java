package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.model.dto.request.ClockInRequest;
import com.hrms.hrms_backend.model.dto.response.AttendanceResponse;
import com.hrms.hrms_backend.model.entity.Attendance;
import com.hrms.hrms_backend.model.entity.Employee;
import com.hrms.hrms_backend.repository.AttendanceRepository;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    // Clock In
    public AttendanceResponse clockIn(Long employeeId, ClockInRequest request) {
        // Check if already clocked in today
        attendanceRepository.findByEmployeeIdAndClockOutIsNull(employeeId)
                .ifPresent(a -> {
                    throw new RuntimeException("Already clocked in. Please clock out first.");
                });

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setClockIn(LocalDateTime.now());
        attendance.setWorkMode(request.getWorkMode() != null ? request.getWorkMode() : "OFFICE");
        attendance.setStatus("PRESENT");

        return mapToResponse(attendanceRepository.save(attendance));
    }

    // Clock Out
    public AttendanceResponse clockOut(Long employeeId) {
        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndClockOutIsNull(employeeId)
                .orElseThrow(() -> new RuntimeException("No active clock-in found"));

        attendance.setClockOut(LocalDateTime.now());
        return mapToResponse(attendanceRepository.save(attendance));
    }

    // Get all attendance for an employee
    public List<AttendanceResponse> getAttendanceByEmployee(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get attendance between dates
    public List<AttendanceResponse> getAttendanceBetween(
            Long employeeId, LocalDateTime start, LocalDateTime end) {
        return attendanceRepository
                .findByEmployeeIdAndClockInBetween(employeeId, start, end)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        AttendanceResponse response = new AttendanceResponse();
        response.setId(attendance.getId());
        response.setEmployeeId(attendance.getEmployee().getId());
        response.setEmployeeName(attendance.getEmployee().getFullName());
        response.setClockIn(attendance.getClockIn());
        response.setClockOut(attendance.getClockOut());
        response.setWorkMode(attendance.getWorkMode());
        response.setStatus(attendance.getStatus());
        return response;
    }
}
