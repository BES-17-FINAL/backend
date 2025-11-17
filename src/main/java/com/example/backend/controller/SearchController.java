// SearchController.java
package com.example.backend.controller;

import com.example.backend.dto.TourAPIResponse;
import com.example.backend.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    // 📌 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> getSpotDetail(
            @RequestParam Long contentId,
            @RequestParam Integer contentTypeId
    ) {
        Map<String, Object> detail = searchService.getSpotDetail(contentId, contentTypeId);
        return ResponseEntity.ok(detail);
    }
}
