package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCreateRequest {

    @NotNull(message = "관광지 ID는 필수입니다.")
    private Long spotId;

    private String spotTitle; // 프론트엔드에서 전달받은 관광지 제목

    private String description;

    @NotNull(message = "시작 날짜는 필수입니다.")
    private LocalDate startDate;

    @NotNull(message = "종료 날짜는 필수입니다.")
    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer orderIndex;
}
