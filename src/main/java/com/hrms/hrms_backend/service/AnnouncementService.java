package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.model.dto.request.AnnouncementRequest;
import com.hrms.hrms_backend.model.dto.response.AnnouncementResponse;
import com.hrms.hrms_backend.model.entity.Announcement;
import com.hrms.hrms_backend.model.entity.Employee;
import com.hrms.hrms_backend.repository.AnnouncementRepository;
import com.hrms.hrms_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final EmployeeRepository employeeRepository;

    public AnnouncementResponse createAnnouncement(Long createdById,
                                                    AnnouncementRequest request) {
        Employee creator = employeeRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setBody(request.getBody());
        announcement.setCreatedBy(creator);
        announcement.setTargetAudience(request.getTargetAudience());

        return mapToResponse(announcementRepository.save(announcement));
    }

    public List<AnnouncementResponse> getAnnouncementsForRole(String role) {
        return announcementRepository
                .findByTargetAudienceInOrderByCreatedAtDesc(List.of("ALL", role))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    private AnnouncementResponse mapToResponse(Announcement announcement) {
        AnnouncementResponse response = new AnnouncementResponse();
        response.setId(announcement.getId());
        response.setTitle(announcement.getTitle());
        response.setBody(announcement.getBody());
        response.setTargetAudience(announcement.getTargetAudience());
        response.setCreatedAt(announcement.getCreatedAt());
        if (announcement.getCreatedBy() != null) {
            response.setCreatedByName(announcement.getCreatedBy().getFullName());
        }
        return response;
    }
}
