package com.hrms.hrms_backend.controller;

import com.hrms.hrms_backend.model.dto.request.DocumentRequest;
import com.hrms.hrms_backend.model.dto.response.DocumentResponse;
import com.hrms.hrms_backend.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // Request document signature — Admin / HR only
    @PostMapping("/request/{requestedById}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<DocumentResponse> requestDocument(
            @PathVariable Long requestedById,
            @Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(
                documentService.requestDocument(requestedById, request));
    }

    // Sign document — employee action
    @PutMapping("/sign/{documentId}")
    public ResponseEntity<DocumentResponse> signDocument(
            @PathVariable Long documentId) {
        return ResponseEntity.ok(documentService.signDocument(documentId));
    }

    // Reject document — employee action
    @PutMapping("/reject/{documentId}")
    public ResponseEntity<DocumentResponse> rejectDocument(
            @PathVariable Long documentId) {
        return ResponseEntity.ok(documentService.rejectDocument(documentId));
    }

    // Get all documents for an employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<DocumentResponse>> getEmployeeDocuments(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(
                documentService.getDocumentsByEmployee(employeeId));
    }

    // Get all pending documents — HR view
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<DocumentResponse>> getPendingDocuments() {
        return ResponseEntity.ok(documentService.getPendingDocuments());
    }
}
