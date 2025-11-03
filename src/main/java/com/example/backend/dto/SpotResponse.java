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

    private String title;
    private String description;
    private LocalDateTime start_at;
    private LocalDateTime end_at;

    public static SpotResponse form(Spot spot, TourAPIResponse tourAPIResponse){
        return SpotResponse.builder()
                .id(spot.getId())
                .type(spot.getType())
                .receive(spot.getReceive())
                .title(tourAPIResponse.getTitle())
                .description(tourAPIResponse.getDescription())
                .start_at(tourAPIResponse.getStart_at())
                .end_at(tourAPIResponse.getEnd_at())
                .build();
    }
}
