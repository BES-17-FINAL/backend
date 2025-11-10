package com.example.backend.service;

import com.example.backend.dto.AttractionReviewRequestDto;
import com.example.backend.dto.AttractionReviewResponseDto;
import com.example.backend.entity.AttractionReview;
import com.example.backend.entity.Spot;
import com.example.backend.entity.User;
import com.example.backend.repository.AttractionReviewRepository;
import com.example.backend.repository.SpotRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttractionReviewService {

    private final AttractionReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final SpotRepository spotRepository;

    public AttractionReviewResponseDto addReview(Long attractionId, Long userId, AttractionReviewRequestDto dto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Spot spot = spotRepository.findById(attractionId)
                .orElseThrow(() -> new RuntimeException("관광지나 축제를 찾을 수 없습니다."));
        AttractionReview review = AttractionReview
                .builder()
                .attractionId(spot)
                .userId(user)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        AttractionReview newReview = reviewRepository.save(review);



        return AttractionReviewResponseDto.from(review);
    }
//    public AttractionReviewResponseDto addReview(Long attractionId, Long userId, AttractionReviewRequestDto dto) {
//        AttractionReview review = AttractionReview.builder()
//                .attractionId(attractionId)
//                .userId(userId)
//                .rating(dto.getRating())
//                .comment(dto.getComment())
//                .build();
//
//        AttractionReview saved = reviewRepository.save(review);
//
//        return AttractionReviewResponseDto.builder()
//                .id(saved.getId())
//                .nickname("User" + userId) // TODO: UserService 연동 시 닉네임 불러오기
//                .rating(saved.getRating())
//                .comment(saved.getComment())
//                .createdAt(saved.getCreatedAt())
//                .build();
//    }
//
//    public List<AttractionReviewResponseDto> getReviews(Long attractionId) {
//        return reviewRepository.findByAttractionId(attractionId).stream()
//                .map(r -> AttractionReviewResponseDto.builder()
//                        .id(r.getId())
//                        .nickname("User" + r.getUserId()) // 닉네임 매핑 필요 시 수정
//                        .rating(r.getRating())
//                        .comment(r.getComment())
//                        .createdAt(r.getCreatedAt())
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//    public Double getAverageRating(Long attractionId) {
//        return reviewRepository.getAverageRating(attractionId);
//    }
}
