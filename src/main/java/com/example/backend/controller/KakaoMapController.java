package com.example.backend.controller;

import com.example.backend.service.KakaoMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kakao-map")
public class KakaoMapController {

    private final KakaoMapService kakaoMapService;

    @GetMapping("/convert-coordinates")
    public ResponseEntity<Map<String, Double>> convertCoordinates(
            @RequestParam double longitude,
            @RequestParam double latitude
    ) {
        try {
            Map<String, Double> result = kakaoMapService.getKakaoCoordinates(longitude, latitude);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search-address")
    public ResponseEntity<Map<String, Object>> searchAddress(
            @RequestParam String address
    ) {
        try {
            Map<String, Object> result = kakaoMapService.searchAddress(address);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

