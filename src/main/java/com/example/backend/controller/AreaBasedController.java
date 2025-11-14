package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class AreaBasedController {

    @Value("${tourism.serviceKey}")
    private String serviceKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/api/area-based")
    public ResponseEntity<String> getAreaBased(
            @RequestParam(defaultValue = "1") String areaCode,             // ⭐ 지역 코드 추가
            @RequestParam(defaultValue = "12") String contentTypeId,      // 관광지로 기본 설정
            @RequestParam(defaultValue = "12") String numOfRows,
            @RequestParam(defaultValue = "1") String pageNo
    ) {
        try {
            String apiUrl = UriComponentsBuilder.fromHttpUrl(
                            "https://apis.data.go.kr/B551011/KorService2/areaBasedList2")
                    .queryParam("ServiceKey", serviceKey)
                    .queryParam("MobileOS", "ETC")
                    .queryParam("MobileApp", "TEST")
                    .queryParam("_type", "json")
                    .queryParam("areaCode", areaCode)              // ⭐ 지역반영
                    .queryParam("contentTypeId", contentTypeId)    // 관광지, 음식점 등
                    .queryParam("numOfRows", numOfRows)
                    .queryParam("pageNo", pageNo)
                    .toUriString();

            String response = restTemplate.getForObject(apiUrl, String.class);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
