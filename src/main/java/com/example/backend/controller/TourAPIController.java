package com.example.backend.controller;

import com.example.backend.dto.TourAPIResponse;
import com.example.backend.service.TourAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class TourAPIController {

    private final TourAPIService tourAPIService;

    // 검색: /api/spots/search?keyword=서울
    @GetMapping("/search")
    public List<TourAPIResponse> searchSpots(@RequestParam String keyword) {
        return tourAPIService.searchSpots(keyword);
    }

    // 상세 조회: /api/spots/{id}
    @GetMapping("/{id}")
    public TourAPIResponse getSpotDetails(@PathVariable Long id) {
        return tourAPIService.getSpotDetails(id);
    }
}
