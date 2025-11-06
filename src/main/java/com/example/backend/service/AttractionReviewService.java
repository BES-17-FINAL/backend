package com.example.backend.service;

import com.example.backend.entity.AttractionReview;
import com.example.backend.repository.AttractionReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttractionReviewService {

    private final AttractionReviewRepository reviewRepository;

    public void addReview(AttractionReview review) {
        reviewRepository.save(review);
    }

    public List<AttractionReview> getReviews(Long attractionId) {
        return reviewRepository.findByAttractionId(attractionId);
    }

    public Double getAverageRating(Long attractionId) {
        Double avg = reviewRepository.getAverageRating(attractionId);
        return avg != null ? avg : 0.0;
    }
}
