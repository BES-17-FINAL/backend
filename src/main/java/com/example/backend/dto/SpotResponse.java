package com.example.backend.dto;

import com.example.backend.entity.Spot;
import com.example.backend.entity.SpotType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotResponse {
    private Long id;
    private SpotType type;
    private float receive;

    private int apiType;
    private String title;
    private String description;
    private String tel;
    private String homepage;
    private String firstImage;
    private String firstImage2;
    private String address;
    private double mapx;
    private double mapy;
    private LocalDateTime start_at;
    private LocalDateTime end_at;

    public static SpotResponse form(Spot spot, TourAPIResponse tourAPIResponse){
        return SpotResponse.builder()
                .id(spot.getId())
                .type(spot.getType())
                .receive(spot.getReceive())

                // ✅ 외부 API 정보 매핑
                .apiType(tourAPIResponse.getApiType())
                .title(tourAPIResponse.getTitle())
                .description(tourAPIResponse.getDescription())
                .tel(tourAPIResponse.getTel())
                .homepage(tourAPIResponse.getHomepage())
                .firstImage(tourAPIResponse.getFirstImage())
                .firstImage2(tourAPIResponse.getFirstImage2())
                .address(tourAPIResponse.getAddress())
                .mapx(tourAPIResponse.getMapx())
                .mapy(tourAPIResponse.getMapy())
                .start_at(tourAPIResponse.getStart_at())
                .end_at(tourAPIResponse.getEnd_at())
                .build();
    }
}
