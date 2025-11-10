package com.example.backend.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttractionReview {
    private Long id;
    private Long userId;
    private Long attractionId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
