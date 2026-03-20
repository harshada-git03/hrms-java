package com.hrms.hrms_backend.controller;

import com.hrms.hrms_backend.service.GeminiService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final GeminiService geminiService;

    @PostMapping("/{employeeId}")
    public ResponseEntity<ChatResponse> chat(
            @PathVariable Long employeeId,
            @RequestBody ChatRequest request) {
        String reply = geminiService.chat(employeeId, request.getMessage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }

    @Data
    public static class ChatRequest {
        private String message;
    }

    @Data
    public static class ChatResponse {
        private final String reply;
    }
}
