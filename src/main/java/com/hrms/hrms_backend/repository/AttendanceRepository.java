package com.hrms.hrms_backend.repository;

import com.hrms.hrms_backend.model.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByEmployeeId(Long employeeId);

    List<Attendance> findByEmployeeIdAndClockInBetween(
            Long employeeId,
            LocalDateTime start,
            LocalDateTime end);

    Optional<Attendance> findByEmployeeIdAndClockOutIsNull(Long employeeId);

    List<Attendance> findByClockInBetweenAndWorkMode(
            LocalDateTime start,
            LocalDateTime end,
            String workMode);
}