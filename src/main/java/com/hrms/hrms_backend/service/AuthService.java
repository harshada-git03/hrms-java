package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.model.dto.request.LoginRequest;
import com.hrms.hrms_backend.model.dto.request.RegisterRequest;
import com.hrms.hrms_backend.model.dto.response.AuthResponse;
import com.hrms.hrms_backend.model.entity.Employee;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import com.hrms.hrms_backend.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create new employee
        Employee employee = new Employee();
        employee.setEmail(request.getEmail());
        employee.setFullName(request.getFullName());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setRole(request.getRole());

        employeeRepository.save(employee);

        // Generate token
        String token = jwtUtil.generateToken(employee.getEmail(), employee.getRole());

        return new AuthResponse(token, employee.getEmail(),
                employee.getFullName(), employee.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        // Find employee by email
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate token
        String token = jwtUtil.generateToken(employee.getEmail(), employee.getRole());

        return new AuthResponse(token, employee.getEmail(),
                employee.getFullName(), employee.getRole());
    }
}
