package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourAPIDetailResponse {
    private String title;
    private String address;
    private String overview;
    private String firstImage;
    private Double mapx;
    private Double mapy;
    private String info;
}
