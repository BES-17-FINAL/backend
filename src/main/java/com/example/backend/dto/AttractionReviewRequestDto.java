package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttractionReviewRequestDto {
    private int rating;
    private String comment;
}