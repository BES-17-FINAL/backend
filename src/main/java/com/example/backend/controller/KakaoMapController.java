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
    public ResponseEntity<?> convertCoordinates(
            @RequestParam double longitude,
            @RequestParam double latitude
    ) {
        try {
            Map<String, Double> result = kakaoMapService.getKakaoCoordinates(longitude, latitude);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "좌표 변환 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/search-address")
    public ResponseEntity<?> searchAddress(
            @RequestParam String address
    ) {
        try {
            Map<String, Object> result = kakaoMapService.searchAddress(address);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            // API 키가 없을 때
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // 기타 에러
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "주소 검색 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}

