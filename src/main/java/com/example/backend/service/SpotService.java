package com.example.backend.service;

import com.example.backend.dto.CnctrRateResponse;
import com.example.backend.dto.SpotResponse;
import com.example.backend.dto.TourAPIResponse;
import com.example.backend.entity.Spot;
import com.example.backend.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SpotService {
    private final SpotRepository spotRepository;
    private final TourAPIService tourAPIService;

    public SpotResponse getSpotById(Long spotId) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("관광지 및 축제를 찾을 수 없습니다."));
        TourAPIResponse response = tourAPIService.getSpotDetails(spot.getApiSpotId());
        return SpotResponse.form(spot, response);
    }

    public List<SpotResponse> getFameSpot() {
        List<Spot> spots = spotRepository.findTop10Sopt();
        List<Long> apiSpotId = spots.stream().map(Spot::getApiSpotId).toList();
        List<TourAPIResponse> tourAPIs = tourAPIService.findSpotByIdList(apiSpotId);

        List<SpotResponse> spotResponses = new ArrayList<>();

        for (int i = 0; i < spots.size(); i++) {
            Spot spot = spots.get(i);
            TourAPIResponse tourAPIResponse = tourAPIs.get(i);
            spotResponses.add(SpotResponse.form(spot, tourAPIResponse));
        }

        return spotResponses;
    }

    public List<CnctrRateResponse> getCnctrRate(Long spotId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("관광지 및 축제를 찾을 수 없습니다."));
        List<Map<String, Object>> cncts = tourAPIService.getcnctrRate(spot.getApiSpotId());
        List<CnctrRateResponse> cnctrRateResponseList = new ArrayList<>();
        for(Map<String, Object> item : cncts){
            LocalDate date = LocalDate.parse(item.get("baseYmd").toString(), formatter);
            CnctrRateResponse cnctrRateResponse = CnctrRateResponse.builder()
                    .cnctrRate(Float.parseFloat(item.get("cnctrRate").toString()))
                    .baseYmd(date)
                    .build();
        }

        return cnctrRateResponseList;
    }
}
