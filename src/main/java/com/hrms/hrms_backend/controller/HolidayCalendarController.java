package com.hrms.hrms_backend.controller;

import com.hrms.hrms_backend.service.HolidayCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class HolidayCalendarController {

    private final HolidayCalendarService holidayCalendarService;

    @GetMapping("/holidays/{year}")
    public ResponseEntity<List<HolidayCalendarService.HolidayDto>> getHolidays(
            @PathVariable int year) {
        return ResponseEntity.ok(holidayCalendarService.getIndianHolidays(year));
    }
}
