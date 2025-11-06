package com.example.backend.dto;

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
}
