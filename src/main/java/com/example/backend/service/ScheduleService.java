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
    private final SpotService spotService;

    @Transactional
    public ScheduleResponse createSchedule(ScheduleCreateRequest request, Long userId) {
        User user = getUserOrThrow(userId);
        Spot spot = spotRepository.findById(request.getSpotId())
                .orElseThrow(() -> new IllegalArgumentException("관광지를 찾을 수 없습니다."));

        // SpotService를 사용하여 spotDetail과 동일한 방식으로 title 가져오기
        String spotTitle;
        try {
            com.example.backend.dto.SpotResponse spotResponse = spotService.getSpotById(request.getSpotId());
            spotTitle = spotResponse.getTitle() != null ? spotResponse.getTitle() : "관광지 #" + spot.getApiSpotId();
        } catch (Exception e) {
            e.printStackTrace();
            spotTitle = "관광지 #" + spot.getApiSpotId();
        }
        
        TravelSchedule schedule = TravelSchedule.builder()
                .user(user)
                .spotId(spot.getId())
                .title(spotTitle) // 실제 관광지 제목 저장
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .orderIndex(request.getOrderIndex())
                .build();

        TravelSchedule saved = travelScheduleRepository.save(schedule);
        return ScheduleResponse.fromEntity(saved, spotTitle);
    }

    public List<ScheduleSummaryResponse> getSchedules(Long userId, LocalDate startDate, LocalDate endDate) {
        try {
            User user = getUserOrThrow(userId);
            List<TravelSchedule> schedules;

            if (startDate != null && endDate != null) {
                schedules = travelScheduleRepository.findAllByUserAndStartDateBetweenOrderByStartDateAscStartTimeAsc(user, startDate, endDate);
            } else {
                schedules = travelScheduleRepository.findAllByUserOrderByStartDateAscStartTimeAsc(user);
            }

            return schedules.stream()
                    .map(schedule -> {
                        try {
                            // SpotService를 사용하여 spotDetail과 동일한 방식으로 title 가져오기
                            System.out.println("일정 조회 - scheduleId: " + schedule.getId() + ", spotId: " + schedule.getSpotId());
                            com.example.backend.dto.SpotResponse spotResponse = spotService.getSpotById(schedule.getSpotId());
                            String spotTitle = spotResponse.getTitle();
                            System.out.println("가져온 spotTitle: " + spotTitle);
                            String finalTitle = spotTitle != null && !spotTitle.trim().isEmpty() ? spotTitle : schedule.getTitle();
                            System.out.println("최종 사용할 title: " + finalTitle);
                            return ScheduleSummaryResponse.fromEntity(schedule, finalTitle);
                        } catch (Exception e) {
                            // SpotService 호출 실패 시 schedule의 title 사용
                            System.err.println("SpotService 호출 실패 - scheduleId: " + schedule.getId() + ", spotId: " + schedule.getSpotId());
                            e.printStackTrace();
                            return ScheduleSummaryResponse.fromEntity(schedule, schedule.getTitle());
                        }
                    })
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("일정 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public ScheduleResponse getSchedule(Long scheduleId, Long userId) {
        try {
            TravelSchedule schedule = getScheduleOrThrow(scheduleId, userId);
            // SpotService를 사용하여 spotDetail과 동일한 방식으로 title 가져오기
            try {
                com.example.backend.dto.SpotResponse spotResponse = spotService.getSpotById(schedule.getSpotId());
                String spotTitle = spotResponse.getTitle();
                return ScheduleResponse.fromEntity(schedule, spotTitle != null ? spotTitle : schedule.getTitle());
            } catch (Exception e) {
                // SpotService 호출 실패 시 schedule의 title 사용
                return ScheduleResponse.fromEntity(schedule, schedule.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("일정 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request, Long userId) {
        try {
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

            // SpotService를 사용하여 spotDetail과 동일한 방식으로 title 가져오기
            try {
                com.example.backend.dto.SpotResponse spotResponse = spotService.getSpotById(schedule.getSpotId());
                String spotTitle = spotResponse.getTitle();
                return ScheduleResponse.fromEntity(schedule, spotTitle != null ? spotTitle : schedule.getTitle());
            } catch (Exception e) {
                // SpotService 호출 실패 시 schedule의 title 사용
                return ScheduleResponse.fromEntity(schedule, schedule.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("일정 수정 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
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

}

