package com.hrms.hrms_backend.controller;

import com.hrms.hrms_backend.model.dto.request.AnnouncementRequest;
import com.hrms.hrms_backend.model.dto.response.AnnouncementResponse;
import com.hrms.hrms_backend.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    // Create announcement — Admin / HR Manager only
    @PostMapping("/{createdById}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @PathVariable Long createdById,
            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(
                announcementService.createAnnouncement(createdById, request));
    }

    // Get announcements for a specific role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<AnnouncementResponse>> getByRole(
            @PathVariable String role) {
        return ResponseEntity.ok(
                announcementService.getAnnouncementsForRole(role));
    }

    // Get all announcements — Admin / HR only
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER')")
    public ResponseEntity<List<AnnouncementResponse>> getAll() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    // Delete — Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}
