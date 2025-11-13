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
    private int apiType;
    private String tel;
    private String homepage;
    private String firstImage;
    private String firstImage2;
    private String description;
    private String address;
    private LocalDateTime start_at;
    private LocalDateTime end_at;
    private double mapx;
    private double mapy;
    private String useTime;
    private String restDate;


}
