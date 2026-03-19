package com.hrms.hrms_backend.service;

import com.hrms.hrms_backend.model.dto.request.HolidayRequest;
import com.hrms.hrms_backend.model.dto.response.HolidayResponse;
import com.hrms.hrms_backend.model.entity.Holiday;
import com.hrms.hrms_backend.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;

    public HolidayResponse addHoliday(HolidayRequest request) {
        Holiday holiday = new Holiday();
        holiday.setName(request.getName());
        holiday.setDate(request.getDate());
        holiday.setType(request.getType());
        holiday.setDescription(request.getDescription());
        return mapToResponse(holidayRepository.save(holiday));
    }

    public List<HolidayResponse> getAllHolidays() {
        return holidayRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteHoliday(Long id) {
        holidayRepository.deleteById(id);
    }

    private HolidayResponse mapToResponse(Holiday holiday) {
        HolidayResponse response = new HolidayResponse();
        response.setId(holiday.getId());
        response.setName(holiday.getName());
        response.setDate(holiday.getDate());
        response.setType(holiday.getType());
        response.setDescription(holiday.getDescription());
        return response;
    }
}
