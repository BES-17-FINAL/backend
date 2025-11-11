package com.example.backend.service;

import com.example.backend.dto.ScheduleCreateRequest;
import com.example.backend.dto.ScheduleResponse;
import com.example.backend.dto.ScheduleSummaryResponse;
import com.example.backend.dto.ScheduleUpdateRequest;
import com.example.backend.entity.Spot;
import com.example.backend.entity.TravelSchedule;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.SpotRepository;
import com.example.backend.repository.TravelScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final TravelScheduleRepository travelScheduleRepository;
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleResponse createSchedule(ScheduleCreateRequest request, Long userId) {
        User user = getUserOrThrow(userId);
        Spot spot = spotRepository.findById(request.getSpotId())
                .orElseThrow(() -> new IllegalArgumentException("관광지를 찾을 수 없습니다."));

        TravelSchedule schedule = TravelSchedule.builder()
                .user(user)
                .spotId(spot.getId())
                .title(spot.getType() != null ? spot.getType().name() : spot.getApiSpotId().toString())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .orderIndex(request.getOrderIndex())
                .build();

        TravelSchedule saved = travelScheduleRepository.save(schedule);
        return ScheduleResponse.fromEntity(saved, getSpotTitle(spot));
    }

    public List<ScheduleSummaryResponse> getSchedules(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = getUserOrThrow(userId);
        List<TravelSchedule> schedules;

        if (startDate != null && endDate != null) {
            schedules = travelScheduleRepository.findAllByUserAndStartDateBetweenOrderByStartDateAscStartTimeAsc(user, startDate, endDate);
        } else {
            schedules = travelScheduleRepository.findAllByUserOrderByStartDateAscStartTimeAsc(user);
        }

        return schedules.stream()
                .map(schedule -> {
                    Spot spot = spotRepository.findById(schedule.getSpotId())
                            .orElseThrow(() -> new IllegalStateException("관광지 정보를 찾을 수 없습니다."));
                    return ScheduleSummaryResponse.fromEntity(schedule, getSpotTitle(spot));
                })
                .toList();
    }

    public ScheduleResponse getSchedule(Long scheduleId, Long userId) {
        TravelSchedule schedule = getScheduleOrThrow(scheduleId, userId);
        Spot spot = spotRepository.findById(schedule.getSpotId())
                .orElseThrow(() -> new IllegalStateException("관광지 정보를 찾을 수 없습니다."));
        return ScheduleResponse.fromEntity(schedule, getSpotTitle(spot));
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request, Long userId) {
        TravelSchedule schedule = getScheduleOrThrow(scheduleId, userId);

        schedule.updateSchedule(
                schedule.getTitle(), // 제목은 기존 값 유지
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getOrderIndex()
        );

        Spot spot = spotRepository.findById(schedule.getSpotId())
                .orElseThrow(() -> new IllegalStateException("관광지 정보를 찾을 수 없습니다."));

        return ScheduleResponse.fromEntity(schedule, getSpotTitle(spot));
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, Long userId) {
        TravelSchedule schedule = getScheduleOrThrow(scheduleId, userId);
        travelScheduleRepository.delete(schedule);
    }

    private TravelSchedule getScheduleOrThrow(Long scheduleId, Long userId) {
        TravelSchedule schedule = travelScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        if (!schedule.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("일정에 접근할 권한이 없습니다.");
        }
        return schedule;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    //관광지 제목
    private String getSpotTitle(Spot spot) {
        return spot.getType() != null ? spot.getType().name() : spot.getApiSpotId().toString();
    }
}

