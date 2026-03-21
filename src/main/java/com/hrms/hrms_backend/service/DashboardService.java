package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.repository.AttendanceRepository;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import com.hrms.hrms_backend.repository.LeaveRequestRepository;
import com.hrms.hrms_backend.repository.PayrollRepository;
import com.hrms.hrms_backend.model.entity.Attendance;
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
    private final PayrollRepository payrollRepository;

    public DashboardSummary getDashboardSummary() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        LocalDate today = LocalDate.now();

        // Total employees
        long totalEmployees = employeeRepository.count();

        // Active employees
        long activeEmployees = employeeRepository.findAll()
                .stream()
                .filter(e -> "ACTIVE".equals(e.getStatus()))
                .count();

        // Who is on approved leave today
        List<LeaveRequest> onLeaveTodayList = leaveRequestRepository
                .findByStatus("APPROVED")
                .stream()
                .filter(l -> !today.isBefore(l.getStartDate())
                          && !today.isAfter(l.getEndDate()))
                .collect(Collectors.toList());

        // All attendance records — filter in Java
        List<Attendance> allAttendance = attendanceRepository.findAll();

        // Present today
        long presentToday = allAttendance.stream()
                .filter(a -> a.getClockIn() != null
                        && !a.getClockIn().isBefore(startOfDay)
                        && !a.getClockIn().isAfter(endOfDay))
                .count();

        // WFH today
        long wfhCount = allAttendance.stream()
                .filter(a -> a.getClockIn() != null
                        && !a.getClockIn().isBefore(startOfDay)
                        && !a.getClockIn().isAfter(endOfDay)
                        && "WFH".equals(a.getWorkMode()))
                .count();

        // In office today
        long inOfficeCount = allAttendance.stream()
                .filter(a -> a.getClockIn() != null
                        && !a.getClockIn().isBefore(startOfDay)
                        && !a.getClockIn().isAfter(endOfDay)
                        && "OFFICE".equals(a.getWorkMode()))
                .count();

        // Pending leave requests
        long pendingLeaves = leaveRequestRepository
                .findByStatus("PENDING")
                .size();

        // Total leaves this month
        long totalLeavesThisMonth = leaveRequestRepository.findAll()
                .stream()
                .filter(l -> l.getAppliedAt() != null
                        && l.getAppliedAt().getMonth() == today.getMonth()
                        && l.getAppliedAt().getYear() == today.getYear())
                .count();

        DashboardSummary summary = new DashboardSummary();
        summary.setTotalEmployees(totalEmployees);
        summary.setActiveEmployees(activeEmployees);
        summary.setPresentToday(presentToday);
        summary.setOnLeaveToday((long) onLeaveTodayList.size());
        summary.setWfhToday(wfhCount);
        summary.setInOfficeToday(inOfficeCount);
        summary.setPendingLeaveRequests(pendingLeaves);
        summary.setTotalLeavesThisMonth(totalLeavesThisMonth);
        summary.setOnLeaveTodayNames(
            onLeaveTodayList.stream()
                .map(l -> l.getEmployee().getFullName())
                .collect(Collectors.toList())
        );

        return summary;
    }

    @Data
    public static class DashboardSummary {
        private long totalEmployees;
        private long activeEmployees;
        private long presentToday;
        private long onLeaveToday;
        private long wfhToday;
        private long inOfficeToday;
        private long pendingLeaveRequests;
        private long totalLeavesThisMonth;
        private List<String> onLeaveTodayNames;
    }
}
