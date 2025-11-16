package com.example.backend.service;

import com.example.backend.dto.SpotReviewRequestDto;
import com.example.backend.dto.SpotReviewResponseDto;
import com.example.backend.entity.Spot;
import com.example.backend.entity.SpotReview;
import com.example.backend.entity.User;
import com.example.backend.repository.SpotRepository;
import com.example.backend.repository.SpotReviewRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SpotReviewService {

    private final SpotReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final SpotRepository spotRepository;

    public SpotReviewResponseDto addReview(Long spotId, Long userId, SpotReviewRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new IllegalArgumentException("관광지를 찾을 수 없습니다."));

        SpotReview review = SpotReview.builder()
                .user(user)
                .spot(spot)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        SpotReview saved = reviewRepository.save(review);
        return SpotReviewResponseDto.from(saved);
    }

    @Transactional(readOnly = true)
    public List<SpotReviewResponseDto> getReviewsBySpot(Long spotId) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getSpot().getId().equals(spotId))
                .map(SpotReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long spotId) {
        List<SpotReview> reviews = reviewRepository.findAll().stream()
                .filter(r -> r.getSpot().getId().equals(spotId))
                .toList();

        if (reviews.isEmpty()) return 0.0;

        double avg = reviews.stream()
                .mapToInt(SpotReview::getRating)
                .average()
                .orElse(0.0);

        return Math.round(avg * 5.0) / 5.0; // 소수점 1자리 반올림
    }
}
