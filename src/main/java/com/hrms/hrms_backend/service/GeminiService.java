package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.model.entity.Employee;
import com.hrms.hrms_backend.repository.AttendanceRepository;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import com.hrms.hrms_backend.repository.LeaveRequestRepository;
import com.hrms.hrms_backend.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
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
        String context = buildContext(employeeId);
        String prompt = context + "\n\nEmployee question: " + userMessage;
        return callGemini(prompt);
    }

    private String buildContext(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        long totalLeaves = leaveRequestRepository.findByEmployeeId(employeeId).size();
        long approvedLeaves = leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, "APPROVED").size();
        long pendingLeaves = leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, "PENDING").size();
        long rejectedLeaves = leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, "REJECTED").size();
        long totalAttendance = attendanceRepository.findByEmployeeId(employeeId).size();

        var payrolls = payrollRepository.findByEmployeeId(employeeId);
        String payrollInfo = payrolls.isEmpty()
                ? "No payroll records yet"
                : "Latest net salary: ₹" + payrolls.get(payrolls.size() - 1).getNetSalary()
                  + " (Month: " + payrolls.get(payrolls.size() - 1).getMonth()
                  + "/" + payrolls.get(payrolls.size() - 1).getYear() + ")";

        // Who is on leave today
        LocalDate today = LocalDate.now();
        long onLeaveToday = leaveRequestRepository.findByStatus("APPROVED")
                .stream()
                .filter(l -> !today.isBefore(l.getStartDate()) && !today.isAfter(l.getEndDate()))
                .count();

        return String.format("""
                You are a helpful HR assistant for NexHR system. You are currently helping %s (Role: %s).
                
                === LIVE DATA FOR THIS EMPLOYEE ===
                - Total leave requests submitted: %d
                - Approved leaves: %d
                - Pending leaves: %d
                - Rejected leaves: %d
                - Total attendance days logged: %d
                - %s
                - Employees on leave today: %d
                - Today's date: %s
                
                === INSTRUCTIONS ===
                - Answer based on the live data above
                - Be concise, friendly and professional
                - For anything outside HR scope, politely say you can only help with HR matters
                - If asked about policies, give general HR best practices
                - Always address the employee by their first name
                """,
                employee.getFullName(),
                employee.getRole(),
                totalLeaves,
                approvedLeaves,
                pendingLeaves,
                rejectedLeaves,
                totalAttendance,
                payrollInfo,
                onLeaveToday,
                today
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

        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                Map response = webClientBuilder.build()
                        .post()
                        .uri(url)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                var candidates = (List) response.get("candidates");
                var content = (Map) ((Map) candidates.get(0)).get("content");
                var parts = (List) content.get("parts");
                return (String) ((Map) parts.get(0)).get("text");

            } catch (Exception e) {
                retryCount++;
                log.warn("Gemini call failed (attempt {}): {}", retryCount, e.getMessage());

                if (e.getMessage() != null && e.getMessage().contains("429")) {
                    try {
                        log.info("Rate limited, waiting {}s before retry...", retryCount * 2);
                        Thread.sleep(2000L * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    return "I encountered an error: " + e.getMessage();
                }
            }
        }
        return "I'm currently busy due to high demand. Please try again in a moment! 🙏";
    }
}
