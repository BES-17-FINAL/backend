package com.example.backend.dto;

import com.example.backend.entity.SpotReview;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotReviewResponseDto {
    private Long id;
    private String nickname;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public static SpotReviewResponseDto from(SpotReview review) {
        String nickname = (review.getUser() != null && review.getUser().getNickname() != null)
                ? review.getUser().getNickname()
                : "알 수 없음";

        return SpotReviewResponseDto.builder()
                .id(review.getId())
                .nickname(nickname)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}