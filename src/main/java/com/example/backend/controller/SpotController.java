package com.example.backend.controller;

import com.example.backend.dto.CnctrRateResponse;
import com.example.backend.dto.SpotResponse;
import com.example.backend.service.SpotService;
import com.example.backend.service.TourAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spot")
public class SpotController {
    private final SpotService spotService;
    @GetMapping("/{spot_id}")
    public ResponseEntity<SpotResponse> getSpot(
            @PathVariable Long spot_id
    ) {
        return ResponseEntity.ok(spotService.getSpotById(spot_id));
    }

    @GetMapping("/fameSpot")
    public ResponseEntity<List<SpotResponse>> getFameSpot() {
        return ResponseEntity.ok(spotService.getFameSpot());
    }

    @GetMapping("/cnctrRate/{spot_id}")
    public List<CnctrRateResponse> getCnctrRate(
            @PathVariable Long spot_id
    ) {

        return spotService.getCnctrRate(spot_id);
    }
}
