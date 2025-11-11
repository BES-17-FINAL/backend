package com.example.backend.dto;

import com.example.backend.entity.TravelSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

//목록용
@Getter
@Builder
@AllArgsConstructor
public class ScheduleSummaryResponse {

    private final Long id;
    private final Long spotId;
    private final String spotTitle;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Integer orderIndex;

    public static ScheduleSummaryResponse fromEntity(TravelSchedule schedule, String spotTitle) {
        return ScheduleSummaryResponse.builder()
                .id(schedule.getId())
                .spotId(schedule.getSpotId())
                .spotTitle(spotTitle)
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .orderIndex(schedule.getOrderIndex())
                .build();
    }
}

