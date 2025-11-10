package com.example.backend.controller;

import com.example.backend.dto.FestivalResponse;
import com.example.backend.service.FestivalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/festival")
public class FestivalController {

    private final FestivalService festivalService;

    public FestivalController(FestivalService festivalService) {
        this.festivalService = festivalService;
    }

    // 상세 조회 — GET /api/festival/{festival_id}
    @GetMapping("/{festival_id}")
    public ResponseEntity<FestivalResponse> getOne(@PathVariable("festival_id") Long id) {
        return ResponseEntity.ok(festivalService.getById(id));
    }

    // 진행중 목록 — GET /api/festival/ongoing?date=YYYY-MM-DD
    @GetMapping("/ongoing")
    public ResponseEntity<List<FestivalResponse>> ongoing(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(festivalService.getOngoing(date));
    }
}