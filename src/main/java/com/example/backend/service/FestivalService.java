package com.example.backend.service;

import com.example.backend.dto.SpotResponse;
import com.example.backend.dto.TourAPIResponse;
import com.example.backend.entity.Spot;
import com.example.backend.entity.SpotType;
import com.example.backend.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalService {

    private final SpotRepository spotRepository;
    private final TourAPIService tourAPIService;

    public SpotResponse getFestivalById(Long festivalId) {
        Spot spot = spotRepository.findById(festivalId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "festival_id=" + festivalId + " not found"));

        if (spot.getType() != SpotType.FESTIVAL) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "festival_id=" + festivalId + " is not FESTIVAL type");
        }

        TourAPIResponse response = tourAPIService.getSpotDetails(spot.getApiSpotId());
        return SpotResponse.form(spot, response);
    }

    public List<SpotResponse> getOngoingFestivals(LocalDate dateOrNull) {
        LocalDate base = (dateOrNull != null)
                ? dateOrNull
                : LocalDate.now(ZoneId.of("Asia/Seoul"));

        List<Spot> festivals = spotRepository.findAll().stream()
                .filter(spot -> spot.getType() == SpotType.FESTIVAL)
                .toList();

        return festivals.stream()
                .map(spot -> {
                    TourAPIResponse api = tourAPIService.getSpotDetails(spot.getApiSpotId());

                    LocalDate start = api.getStart_at() != null
                            ? api.getStart_at().toLocalDate()
                            : null;
                    LocalDate end = api.getEnd_at() != null
                            ? api.getEnd_at().toLocalDate()
                            : null;

                    if (start != null && start.isAfter(base)) return null; // 시작 전
                    if (end != null && end.isBefore(base)) return null;    // 종료

                    return SpotResponse.form(spot, api);
                })
                .filter(res -> res != null)
                .toList();
    }
}