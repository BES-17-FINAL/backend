package com.example.backend.controller;

import com.example.backend.dto.AttractionReviewRequestDto;
import com.example.backend.dto.AttractionReviewResponseDto;
import com.example.backend.service.AttractionReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attractions")
public class AttractionReviewController {

    private final AttractionReviewService reviewService;

    @PostMapping("/{attractionId}/reviews")
    public ResponseEntity<AttractionReviewResponseDto> addReview(
            @PathVariable Long attractionId,
            @RequestBody AttractionReviewRequestDto request,
            @RequestAttribute("userId") Long userId // JWT 필터에서 설정
    ) {
        return ResponseEntity.ok(reviewService.addReview(attractionId, userId, request));
    }

    @GetMapping("/{attractionId}/reviews")
    public ResponseEntity<List<AttractionReviewResponseDto>> getReviews(@PathVariable Long attractionId) {
        return ResponseEntity.ok(reviewService.getReviews(attractionId));
    }

    @GetMapping("/{attractionId}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long attractionId) {
        return ResponseEntity.ok(reviewService.getAverageRating(attractionId));
    }
}
