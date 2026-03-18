package com.hrms.hrms_backend.repository;

import com.hrms.hrms_backend.model.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Get all attendance for one employee
    List<Attendance> findByEmployeeId(Long employeeId);

    // Get attendance between two dates for an employee
    List<Attendance> findByEmployeeIdAndClockInBetween(
            Long employeeId,
            LocalDateTime start,
            LocalDateTime end);

    // Find today's open attendance (clocked in but not out yet)
    Optional<Attendance> findByEmployeeIdAndClockOutIsNull(Long employeeId);
}
