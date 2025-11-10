package com.example.backend.dto;

import com.example.backend.entity.AttractionReview;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttractionReviewResponseDto {
    private Long id;
    private String nickname;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public static AttractionReviewResponseDto from(AttractionReview attractionReview) {
        return AttractionReviewResponseDto.builder()
                .id(attractionReview.getId())
                .comment(attractionReview.getComment())
                .createdAt(attractionReview.getCreatedAt())
                .nickname(attractionReview.getUserId().getNickname())
                .rating(attractionReview.getRating())
                .build();
    }
}
