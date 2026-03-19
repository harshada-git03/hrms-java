package com.hrms.hrms_backend.controller;

import com.hrms.hrms_backend.model.dto.request.HolidayRequest;
import com.hrms.hrms_backend.model.dto.response.HolidayResponse;
import com.hrms.hrms_backend.service.HolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    // Add holiday — Admin only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HolidayResponse> addHoliday(
            @Valid @RequestBody HolidayRequest request) {
        return ResponseEntity.ok(holidayService.addHoliday(request));
    }

    // Get all holidays — everyone
    @GetMapping
    public ResponseEntity<List<HolidayResponse>> getAllHolidays() {
        return ResponseEntity.ok(holidayService.getAllHolidays());
    }

    // Delete holiday — Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.noContent().build();
    }
}
