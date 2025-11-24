package com.example.backend.controller;

import com.example.backend.dto.ScheduleCreateRequest;
import com.example.backend.dto.ScheduleResponse;
import com.example.backend.dto.ScheduleSummaryResponse;
import com.example.backend.dto.ScheduleUpdateRequest;
import com.example.backend.service.ScheduleService;
import com.example.backend.service.UserService;
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
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createSchedule(
            @Valid @RequestBody ScheduleCreateRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email
    ) {
        try {
            // userId와 email이 모두 null인 경우 체크
            if (userId == null && email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("인증이 필요합니다."));
            }

            Long resolvedUserId = userService.resolveUserId(userId, email);
            ScheduleResponse response = scheduleService.createSchedule(request, resolvedUserId);
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
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        try {
            // CRITICAL FIX: 인증 정보 체크
            if (userId == null && email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("인증이 필요합니다. 로그인 후 다시 시도해주세요."));
            }

            Long resolvedUserId = userService.resolveUserId(userId, email);

            // resolvedUserId가 null인 경우 추가 체크
            if (resolvedUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("사용자 정보를 확인할 수 없습니다."));
            }

            List<ScheduleSummaryResponse> schedules = scheduleService.getSchedules(resolvedUserId, startDate, endDate);
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
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email
    ) {
        try {
            if (userId == null && email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("인증이 필요합니다."));
            }

            Long resolvedUserId = userService.resolveUserId(userId, email);
            ScheduleResponse response = scheduleService.getSchedule(scheduleId, resolvedUserId);
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
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email
    ) {
        try {
            if (userId == null && email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("인증이 필요합니다."));
            }

            Long resolvedUserId = userService.resolveUserId(userId, email);
            ScheduleResponse response = scheduleService.updateSchedule(scheduleId, request, resolvedUserId);
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
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email
    ) {
        try {
            if (userId == null && email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("인증이 필요합니다."));
            }

            Long resolvedUserId = userService.resolveUserId(userId, email);
            scheduleService.deleteSchedule(scheduleId, resolvedUserId);
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

    // ErrorResponse를 public static으로 변경
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