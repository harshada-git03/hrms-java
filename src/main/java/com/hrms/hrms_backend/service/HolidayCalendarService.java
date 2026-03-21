package com.hrms.hrms_backend.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayCalendarService {

    @Value("${app.calendarific.api-key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;

    public List<HolidayDto> getIndianHolidays(int year) {
        try {
            String url = "https://calendarific.com/api/v2/holidays"
                    + "?api_key=" + apiKey
                    + "&country=IN"
                    + "&year=" + year
                    + "&type=national,local";

            Map response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<HolidayDto> holidays = new ArrayList<>();

            if (response != null && response.containsKey("response")) {
                Map responseBody = (Map) response.get("response");
                List<Map> holidayList = (List<Map>) responseBody.get("holidays");

                if (holidayList != null) {
                    for (Map holiday : holidayList) {
                        HolidayDto dto = new HolidayDto();
                        dto.setName((String) holiday.get("name"));
                        dto.setDescription((String) holiday.get("description"));

                        Map dateMap = (Map) holiday.get("date");
                        if (dateMap != null) {
                            Map isoMap = (Map) dateMap.get("datetime");
                            if (isoMap != null) {
                                int month = (Integer) isoMap.get("month");
                                int day = (Integer) isoMap.get("day");
                                dto.setDate(String.format("%04d-%02d-%02d", year, month, day));
                                dto.setMonth(month);
                                dto.setDay(day);
                            }
                        }

                        List<Map> typeList = (List<Map>) holiday.get("type");
                        if (typeList != null && !typeList.isEmpty()) {
                            dto.setType((String) typeList.get(0).get("name"));
                        }

                        holidays.add(dto);
                    }
                }
            }

            return holidays;

        } catch (Exception e) {
            log.error("Failed to fetch holidays from Calendarific: {}", e.getMessage());
            return getFallbackHolidays(year);
        }
    }

    // Fallback in case API fails
    private List<HolidayDto> getFallbackHolidays(int year) {
        List<HolidayDto> holidays = new ArrayList<>();
        String[][] data = {
            {"Republic Day", year + "-01-26", "01", "26", "National Holiday"},
            {"Holi", year + "-03-14", "03", "14", "Hindu Festival"},
            {"Ram Navami", year + "-04-06", "04", "06", "Hindu Festival"},
            {"Ambedkar Jayanti", year + "-04-14", "04", "14", "National Holiday"},
            {"Good Friday", year + "-04-18", "04", "18", "Christian Holiday"},
            {"Maharashtra Day", year + "-05-01", "05", "01", "Regional Holiday"},
            {"Buddha Purnima", year + "-05-12", "05", "12", "Buddhist Festival"},
            {"Eid ul-Fitr", year + "-03-31", "03", "31", "Muslim Festival"},
            {"Independence Day", year + "-08-15", "08", "15", "National Holiday"},
            {"Ganesh Chaturthi", year + "-08-27", "08", "27", "Hindu Festival"},
            {"Gandhi Jayanti", year + "-10-02", "10", "02", "National Holiday"},
            {"Dussehra", year + "-10-02", "10", "02", "Hindu Festival"},
            {"Diwali", year + "-10-20", "10", "20", "Hindu Festival"},
            {"Christmas", year + "-12-25", "12", "25", "Christian Holiday"},
        };
        for (String[] d : data) {
            HolidayDto dto = new HolidayDto();
            dto.setName(d[0]);
            dto.setDate(d[1]);
            dto.setMonth(Integer.parseInt(d[2]));
            dto.setDay(Integer.parseInt(d[3]));
            dto.setType(d[4]);
            dto.setDescription(d[0] + " - " + d[4]);
            holidays.add(dto);
        }
        return holidays;
    }

    @Data
    public static class HolidayDto {
        private String name;
        private String description;
        private String date;
        private int month;
        private int day;
        private String type;
    }
}
