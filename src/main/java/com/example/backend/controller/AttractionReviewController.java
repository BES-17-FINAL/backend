package com.example.backend.controller;

import com.example.backend.entity.AttractionReview;
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
    public ResponseEntity<?> addReview(@PathVariable Long attractionId,
                                       @RequestBody AttractionReview review,
                                       @RequestAttribute("userId") Long userId) {
        review.setAttractionId(attractionId);
        review.setUserId(userId);
        reviewService.addReview(review);
        return ResponseEntity.ok("리뷰 등록 완료");
    }

    @GetMapping("/{attractionId}/reviews")
    public ResponseEntity<List<AttractionReview>> getReviews(@PathVariable Long attractionId) {
        return ResponseEntity.ok(reviewService.getReviews(attractionId));
    }

    @GetMapping("/{attractionId}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long attractionId) {
        return ResponseEntity.ok(reviewService.getAverageRating(attractionId));
    }
}
