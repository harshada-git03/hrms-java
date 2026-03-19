package com.hrms.hrms_backend.repository;

import com.hrms.hrms_backend.model.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // All leaves for one employee
    List<LeaveRequest> findByEmployeeId(Long employeeId);

    // All pending leaves — for HR manager to review
    List<LeaveRequest> findByStatus(String status);

    // All leaves for an employee filtered by status
    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, String status);

    // All leaves of a specific type for an employee
    List<LeaveRequest> findByEmployeeIdAndLeaveType(Long employeeId, String leaveType);
}
