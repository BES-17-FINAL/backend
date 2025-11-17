package com.example.backend.controller;

import com.example.backend.dto.SpotResponse;
import com.example.backend.service.FestivalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festival")
public class FestivalController {

    private final FestivalService festivalService;

    // 상세 조회 : GET /api/festival/{festival_id}
    @GetMapping("/{festival_id}")
    public ResponseEntity<SpotResponse> getFestival(
            @PathVariable("festival_id") Long festivalId
    ) {
        return ResponseEntity.ok(festivalService.getFestivalById(festivalId));
    }

    // 진행중 축제 목록 : GET /api/festival/ongoing?date=YYYY-MM-DD
    @GetMapping("/ongoing")
    public ResponseEntity<List<SpotResponse>> getOngoingFestivals(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(festivalService.getOngoingFestivals(date));
    }
}