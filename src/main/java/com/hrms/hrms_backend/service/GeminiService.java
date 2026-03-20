package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.model.entity.Employee;
import com.hrms.hrms_backend.repository.AttendanceRepository;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import com.hrms.hrms_backend.repository.LeaveRequestRepository;
import com.hrms.hrms_backend.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.model}")
    private String model;

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollRepository payrollRepository;
    private final WebClient.Builder webClientBuilder;

    public String chat(Long employeeId, String userMessage) {
        // 1. Build context from employee's real data
        String context = buildContext(employeeId);

        // 2. Build the full prompt
        String prompt = context + "\n\nEmployee question: " + userMessage;

        // 3. Call Gemini API
        return callGemini(prompt);
    }

    private String buildContext(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Leave summary
        long totalLeaves = leaveRequestRepository
                .findByEmployeeId(employeeId).size();
        long approvedLeaves = leaveRequestRepository
                .findByEmployeeIdAndStatus(employeeId, "APPROVED").size();
        long pendingLeaves = leaveRequestRepository
                .findByEmployeeIdAndStatus(employeeId, "PENDING").size();

        // Attendance summary
        long totalAttendance = attendanceRepository
                .findByEmployeeId(employeeId).size();

        // Latest payroll
        var payrolls = payrollRepository.findByEmployeeId(employeeId);
        String payrollInfo = payrolls.isEmpty() ? "No payroll records yet" :
                "Latest net salary: ₹" + payrolls.get(payrolls.size() - 1).getNetSalary();

        return String.format("""
                You are an HR assistant for NexHR. You are helping %s (%s role).
                
                Their current data:
                - Total leave requests: %d
                - Approved leaves: %d
                - Pending leaves: %d
                - Total days attended: %d
                - %s
                - Today's date: %s
                
                Answer their HR related questions based on this data.
                Be helpful, professional and concise.
                If asked something outside HR scope, politely redirect.
                """,
                employee.getFullName(),
                employee.getRole(),
                totalLeaves,
                approvedLeaves,
                pendingLeaves,
                totalAttendance,
                payrollInfo,
                LocalDate.now()
        );
    }

    private String callGemini(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            Map response = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Extract text from Gemini response
            var candidates = (List) response.get("candidates");
            var content = (Map) ((Map) candidates.get(0)).get("content");
            var parts = (List) content.get("parts");
            return (String) ((Map) parts.get(0)).get("text");

       } catch (Exception e) {
    return "Error: " + e.getMessage();
}
    }
}
