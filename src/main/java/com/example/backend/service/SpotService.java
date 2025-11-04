package com.example.backend.service;

import com.example.backend.dto.SpotResponse;
import com.example.backend.dto.TourAPIResponse;
import com.example.backend.entity.Spot;
import com.example.backend.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpotService {
    private final SpotRepository spotRepository;
    private final TourAPIService tourAPIService;

    public SpotResponse getSpotById(Long spotId) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("관광지 및 축제를 찾을 수 없습니다."));
        TourAPIResponse response = tourAPIService.getSpotDetails(spot.getApiSpotId());
        return SpotResponse.form(spot, response);
    }
}
