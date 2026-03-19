package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.repository.AttendanceRepository;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import com.hrms.hrms_backend.repository.LeaveRequestRepository;
import com.hrms.hrms_backend.model.entity.LeaveRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public DashboardSummary getDashboardSummary() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        LocalDate today = LocalDate.now();

        // Total employees
        long totalEmployees = employeeRepository.count();

        // Who is on approved leave today
        List<LeaveRequest> onLeaveToday = leaveRequestRepository
                .findByStatus("APPROVED")
                .stream()
                .filter(l -> !today.isBefore(l.getStartDate())
                          && !today.isAfter(l.getEndDate()))
                .collect(Collectors.toList());

        // WFH today
        long wfhCount = attendanceRepository
                .findByClockInBetweenAndWorkMode(startOfDay, endOfDay, "WFH")
                .size();

        // In office today
        long inOfficeCount = attendanceRepository
                .findByClockInBetweenAndWorkMode(startOfDay, endOfDay, "OFFICE")
                .size();

        DashboardSummary summary = new DashboardSummary();
        summary.setTotalEmployees(totalEmployees);
        summary.setOnLeaveToday((long) onLeaveToday.size());
        summary.setWfhToday(wfhCount);
        summary.setInOfficeToday(inOfficeCount);
        summary.setOnLeaveTodayNames(
            onLeaveToday.stream()
                .map(l -> l.getEmployee().getFullName())
                .collect(Collectors.toList())
        );

        return summary;
    }

    @Data
    public static class DashboardSummary {
        private long totalEmployees;
        private long onLeaveToday;
        private long wfhToday;
        private long inOfficeToday;
        private List<String> onLeaveTodayNames;
    }
}