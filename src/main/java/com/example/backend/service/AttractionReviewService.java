package com.example.backend.service;

import com.example.backend.dto.AttractionReviewRequestDto;
import com.example.backend.dto.AttractionReviewResponseDto;
import com.example.backend.entity.AttractionReview;
import com.example.backend.repository.AttractionReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttractionReviewService {

    private final AttractionReviewRepository reviewRepository;

    public AttractionReviewResponseDto addReview(Long attractionId, Long userId, AttractionReviewRequestDto dto) {
        AttractionReview review = AttractionReview.builder()
                .attractionId(attractionId)
                .userId(userId)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        AttractionReview saved = reviewRepository.save(review);

        return AttractionReviewResponseDto.builder()
                .id(saved.getId())
                .nickname("User" + userId) // TODO: UserService 연동 시 닉네임 불러오기
                .rating(saved.getRating())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public List<AttractionReviewResponseDto> getReviews(Long attractionId) {
        return reviewRepository.findByAttractionId(attractionId).stream()
                .map(r -> AttractionReviewResponseDto.builder()
                        .id(r.getId())
                        .nickname("User" + r.getUserId()) // 닉네임 매핑 필요 시 수정
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public Double getAverageRating(Long attractionId) {
        return reviewRepository.getAverageRating(attractionId);
    }
}
