package com.hrms.hrms_backend.repository;

import com.hrms.hrms_backend.model.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    // Get all payroll records for one employee
    List<Payroll> findByEmployeeId(Long employeeId);

    // Get specific month/year payroll for an employee
    Optional<Payroll> findByEmployeeIdAndMonthAndYear(Long employeeId, int month, int year);

    // Get all payroll for a specific month/year — for HR
    List<Payroll> findByMonthAndYear(int month, int year);
}
