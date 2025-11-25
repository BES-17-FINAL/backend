package com.example.backend.controller;

import com.example.backend.dto.ScheduleCreateRequest;
import com.example.backend.dto.ScheduleResponse;
import com.example.backend.dto.ScheduleSummaryResponse;
import com.example.backend.dto.ScheduleUpdateRequest;
import com.example.backend.entity.User;
import com.example.backend.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<?> createSchedule(
            @Valid @RequestBody ScheduleCreateRequest request,
            @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("인증이 필요합니다."));
            }

            Long userId = user.getUserId();
            ScheduleResponse response = scheduleService.createSchedule(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("일정 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getSchedules(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("인증이 필요합니다. 로그인 후 다시 시도해주세요."));
            }

            Long userId = user.getUserId();
            List<ScheduleSummaryResponse> schedules = scheduleService.getSchedules(userId, startDate, endDate);
            return ResponseEntity.ok(schedules);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("일정 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("인증이 필요합니다."));
            }

            Long userId = user.getUserId();
            ScheduleResponse response = scheduleService.getSchedule(scheduleId, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("일정 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleUpdateRequest request,
            @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("인증이 필요합니다."));
            }

            Long userId = user.getUserId();
            ScheduleResponse response = scheduleService.updateSchedule(scheduleId, request, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("일정 수정 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("인증이 필요합니다."));
            }

            Long userId = user.getUserId();
            scheduleService.deleteSchedule(scheduleId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("일정 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
