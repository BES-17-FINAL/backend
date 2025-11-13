package com.example.backend.controller;

import com.example.backend.dto.TourAPIResponse;
import com.example.backend.service.SearchService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // React 개발 서버 주소
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search")
    public ResponseEntity<List<TourAPIResponse>> searchSpots(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer contentTypeId
    ) {
        List<TourAPIResponse> results = searchService.searchSpots(keyword, contentTypeId);
        return ResponseEntity.ok(results);
    }

}
