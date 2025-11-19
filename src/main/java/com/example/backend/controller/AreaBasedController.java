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
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String sigunguCode,
            @RequestParam(required = false) String contentTypeId,
            @RequestParam(defaultValue = "20") String numOfRows,
            @RequestParam(defaultValue = "1") String pageNo
    ) {
        try {

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/areaBasedList2")
                    .queryParam("ServiceKey", serviceKey)
                    .queryParam("MobileOS", "ETC")
                    .queryParam("MobileApp", "TRAVELHUB")
                    .queryParam("_type", "json")
                    .queryParam("numOfRows", numOfRows)
                    .queryParam("pageNo", pageNo);

            // ⭐ 지역(areaCode) 들어오면 추가
            if (areaCode != null && !areaCode.isEmpty()) {
                builder.queryParam("areaCode", areaCode);
            }

            // ⭐ 시군구(sigunguCode) 들어오면 추가
            if (sigunguCode != null && !sigunguCode.isEmpty()) {
                builder.queryParam("sigunguCode", sigunguCode);
            }

            // ⭐ 콘텐츠 타입(contentTypeId) 들어오면 추가
            if (contentTypeId != null && !contentTypeId.isEmpty()) {
                builder.queryParam("contentTypeId", contentTypeId);
            }

            String apiUrl = builder.toUriString();
            String response = restTemplate.getForObject(apiUrl, String.class);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
