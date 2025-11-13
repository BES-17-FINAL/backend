// SearchController.java
package com.example.backend.controller;

import com.example.backend.dto.TourAPIResponse;
import com.example.backend.service.SearchService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // React dev 서버 허용
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public List<TourAPIResponse> searchSpots(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer contentTypeId
    ) {
        return searchService.searchSpots(keyword, contentTypeId);
    }
}
