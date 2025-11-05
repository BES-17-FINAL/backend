package com.example.backend.service;

import com.example.backend.dto.TourAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TourAPIService {
    private final WebClient webClient;

    @Value("${secret_key}")
    private String secretKey;

    public TourAPIResponse getSpotDetails(Long id) {
        Map<String, Object> spotJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/B551011/KorService2/detailCommon2")
                        .queryParam("ServiceKey", secretKey)
                        .queryParam("contentId", id)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "TRAVELHUB")
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map<String, Object> response = (Map<String, Object>) spotJson.get("response");
        Map<String, Object> body = (Map<String, Object>) response.get("body");
        Map<String, Object> items = (Map<String, Object>) body.get("items");
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");
        Map<String, Object> item = itemList.get(0);
        String mapxStr = (String) item.get("mapx");
        String mapyStr = (String) item.get("mapy");

        Double mapx = mapxStr != null ? Double.parseDouble(mapxStr) : null;
        Double mapy = mapyStr != null ? Double.parseDouble(mapyStr) : null;

        return TourAPIResponse.builder()
                .title((String) item.get("title"))
                .description((String) item.get("overview"))
                .mapx(mapx)
                .mapy(mapy)
                .build();
    }
}
/*
Map<String, Object> spotJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/B551011/KorService2/detailIntro2")
                        .queryParam("ServiceKey", secretKey)
                        .queryParam("contentTypeId", item.get("contentTypeId")
                        .queryParam("contentId", id)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "TRAVELHUB")
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
세부정보
Map<String, Object> spotJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/B551011/KorService2/detailInfo2")
                        .queryParam("ServiceKey", secretKey)
                        .queryParam("contentTypeId", item.get("contentTypeId")
                        .queryParam("contentId", id)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "TRAVELHUB")
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
 */