package com.example.backend.controller;

import com.example.backend.dto.SpotReviewRequestDto;
import com.example.backend.dto.SpotReviewResponseDto;
import com.example.backend.entity.User;
import com.example.backend.service.SpotReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spots")
public class SpotReviewController {

    private final SpotReviewService reviewService;

    @PostMapping("/{spotId}/reviews")
    public ResponseEntity<SpotReviewResponseDto> addReview(
            @PathVariable Long spotId,
            @RequestBody SpotReviewRequestDto request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getUserId();

        return ResponseEntity.ok(
                reviewService.addReview(spotId, userId, request)
        );
    }

    @GetMapping("/{spotId}/reviews")
    public ResponseEntity<List<SpotReviewResponseDto>> getReviews(
            @PathVariable Long spotId
    ) {
        return ResponseEntity.ok(reviewService.getReviewsBySpot(spotId));
    }

    @GetMapping("/{spotId}/rating")
    public ResponseEntity<Double> getAverageRating(
            @PathVariable Long spotId
    ) {
        return ResponseEntity.ok(reviewService.getAverageRating(spotId));
    }
}
