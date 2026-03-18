package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.model.dto.request.UpdateEmployeeRequest;
import com.hrms.hrms_backend.model.dto.response.EmployeeResponse;
import com.hrms.hrms_backend.model.entity.Employee;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Get all employees
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get single employee by ID
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(employee);
    }

    // Update employee
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (request.getFullName() != null) employee.setFullName(request.getFullName());
        if (request.getRole() != null) employee.setRole(request.getRole());
        if (request.getStatus() != null) employee.setStatus(request.getStatus());

        employeeRepository.save(employee);
        return mapToResponse(employee);
    }

    // Delete employee
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    // Convert Employee entity to EmployeeResponse DTO
    private EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setFullName(employee.getFullName());
        response.setEmail(employee.getEmail());
        response.setRole(employee.getRole());
        response.setStatus(employee.getStatus());
        response.setCreatedAt(employee.getCreatedAt());
        return response;
    }
}
