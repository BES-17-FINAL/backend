package com.example.backend.dto;

import com.example.backend.entity.TravelSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

//일정 상세
@Getter
@Builder
@AllArgsConstructor
public class ScheduleResponse {

    private final Long id;
    private final Long spotId;
    private final String spotTitle;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Integer orderIndex;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ScheduleResponse fromEntity(TravelSchedule schedule, String spotTitle) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .spotId(schedule.getSpotId())
                .spotTitle(spotTitle != null ? spotTitle : schedule.getTitle())
                .description(schedule.getDescription())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .orderIndex(schedule.getOrderIndex())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}
