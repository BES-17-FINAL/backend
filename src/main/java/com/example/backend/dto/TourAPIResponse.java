package com.example.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TourAPIResponse {
    private String title;
    private String description;
    private LocalDateTime start_at;
    private LocalDateTime end_at;
    private double mapx;
    private double mapy;
}
