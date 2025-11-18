// SearchController.java
package com.example.backend.controller;

import com.example.backend.dto.TourAPIResponse;
import com.example.backend.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.dto.TourAPIDetailResponse;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    // 🔍 검색 기능
    @GetMapping("")
    public ResponseEntity<List<TourAPIResponse>> searchSpots(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer contentTypeId
    ) {
        List<TourAPIResponse> result = searchService.searchSpots(keyword, contentTypeId);
        return ResponseEntity.ok(result);
    }

    // 📌 상세 조회 기능
    @GetMapping("/{contentId}")
    public ResponseEntity<TourAPIDetailResponse> getSpotDetail(
            @PathVariable Long contentId,
            @RequestParam Integer contentTypeId
    ) {
        TourAPIDetailResponse detail = searchService.getSpotDetail(contentId, contentTypeId);
        if (detail != null) {
            return ResponseEntity.ok(detail);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
