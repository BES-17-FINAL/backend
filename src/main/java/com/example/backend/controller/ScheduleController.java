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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @Valid @RequestBody ScheduleCreateRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email
    ) {
        Long resolvedUserId = userService.resolveUserId(userId, email);
        ScheduleResponse response = scheduleService.createSchedule(request, resolvedUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleSummaryResponse>> getSchedules(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Long resolvedUserId = userService.resolveUserId(userId, email);
        List<ScheduleSummaryResponse> schedules = scheduleService.getSchedules(resolvedUserId, startDate, endDate);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email
    ) {
        Long resolvedUserId = userService.resolveUserId(userId, email);
        ScheduleResponse response = scheduleService.getSchedule(scheduleId, resolvedUserId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleUpdateRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email
    ) {
        Long resolvedUserId = userService.resolveUserId(userId, email);
        ScheduleResponse response = scheduleService.updateSchedule(scheduleId, request, resolvedUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email
    ) {
        Long resolvedUserId = userService.resolveUserId(userId, email);
        scheduleService.deleteSchedule(scheduleId, resolvedUserId);
        return ResponseEntity.noContent().build();
    }
}

