package com.example.backend.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AttractionReview {
    private Long id;
    private Long userId;
    private Long attractionId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
