package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.model.dto.request.DocumentRequest;
import com.hrms.hrms_backend.model.dto.response.DocumentResponse;
import com.hrms.hrms_backend.model.entity.Document;
import com.hrms.hrms_backend.model.entity.Employee;
import com.hrms.hrms_backend.repository.DocumentRepository;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;

    // Request a document signature
    public DocumentResponse requestDocument(Long requestedById,
                                             DocumentRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employee requestedBy = employeeRepository.findById(requestedById)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        Document document = new Document();
        document.setEmployee(employee);
        document.setRequestedBy(requestedBy);
        document.setTitle(request.getTitle());
        document.setType(request.getType());
        document.setFileUrl(request.getFileUrl());
        document.setRemarks(request.getRemarks());

        return mapToResponse(documentRepository.save(document));
    }

    // Employee signs the document
    public DocumentResponse signDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!document.getStatus().equals("PENDING_SIGNATURE")) {
            throw new RuntimeException("Document already processed");
        }

        document.setStatus("SIGNED");
        document.setSignedAt(LocalDateTime.now());
        return mapToResponse(documentRepository.save(document));
    }

    // Employee rejects the document
    public DocumentResponse rejectDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setStatus("REJECTED");
        return mapToResponse(documentRepository.save(document));
    }

    // Get all documents for an employee
    public List<DocumentResponse> getDocumentsByEmployee(Long employeeId) {
        return documentRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get all pending documents — HR view
    public List<DocumentResponse> getPendingDocuments() {
        return documentRepository.findByStatus("PENDING_SIGNATURE")
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DocumentResponse mapToResponse(Document document) {
        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setEmployeeId(document.getEmployee().getId());
        response.setEmployeeName(document.getEmployee().getFullName());
        response.setTitle(document.getTitle());
        response.setType(document.getType());
        response.setStatus(document.getStatus());
        response.setFileUrl(document.getFileUrl());
        response.setRemarks(document.getRemarks());
        response.setRequestedAt(document.getRequestedAt());
        response.setSignedAt(document.getSignedAt());
        if (document.getRequestedBy() != null) {
            response.setRequestedByName(document.getRequestedBy().getFullName());
        }
        return response;
    }
}
