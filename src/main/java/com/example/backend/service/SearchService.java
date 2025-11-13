// SearchService.java
package com.example.backend.service;

import com.example.backend.dto.TourAPIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class SearchService {

    @Value("${tourism.serviceKey}")
    private String secretKey;

    private final WebClient webClient = WebClient.builder().build();

    // 지역명 -> areaCode 매핑
    private static final Map<String, Integer> REGION_CODE_MAP = Map.ofEntries(
            Map.entry("서울", 1),
            Map.entry("부산", 6),
            Map.entry("대구", 7),
            Map.entry("인천", 2),
            Map.entry("광주", 5),
            Map.entry("대전", 4),
            Map.entry("울산", 8),
            Map.entry("세종", 3),
            Map.entry("경기", 31),
            Map.entry("강원", 32),
            Map.entry("충북", 33),
            Map.entry("충남", 34),
            Map.entry("전북", 35),
            Map.entry("전남", 36),
            Map.entry("경북", 37),
            Map.entry("경남", 38),
            Map.entry("제주", 39)
    );

    public List<TourAPIResponse> searchSpots(String keyword, Integer contentTypeId) {
        Integer areaCode = REGION_CODE_MAP.get(keyword);

        Map<String, Object> searchJson = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.scheme("https")
                            .host("apis.data.go.kr")
                            .path("/B551011/KorService2/areaBasedList2")
                            .queryParam("ServiceKey", secretKey)
                            .queryParam("MobileOS", "WEB")
                            .queryParam("MobileApp", "TRAVELHUB")
                            .queryParam("_type", "json")
                            .queryParam("numOfRows", 20)
                            .queryParam("pageNo", 1);

                    if (contentTypeId != null) uriBuilder.queryParam("contentTypeId", contentTypeId);
                    if (areaCode != null) uriBuilder.queryParam("areaCode", areaCode);

                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<TourAPIResponse> resultList = new ArrayList<>();
        if (searchJson == null) return resultList;

        Map<String, Object> response = (Map<String, Object>) searchJson.get("response");
        Map<String, Object> body = (Map<String, Object>) response.get("body");
        Map<String, Object> items = (Map<String, Object>) body.get("items");

        if (items == null) return resultList;

        Object itemObj = items.get("item");
        List<Map<String, Object>> itemList = new ArrayList<>();
        if (itemObj instanceof List) {
            itemList = (List<Map<String, Object>>) itemObj;
        } else if (itemObj instanceof Map) {
            itemList.add((Map<String, Object>) itemObj);
        }

        for (Map<String, Object> item : itemList) {
            TourAPIResponse dto = TourAPIResponse.builder()
                    .title((String) item.get("title"))
                    .apiType(item.get("contenttypeid") != null ? Integer.parseInt(item.get("contenttypeid").toString()) : null)
                    .address((String) item.get("addr1"))
                    .firstImage((String) item.get("firstimage"))
                    .mapx(item.get("mapx") != null ? Double.parseDouble(item.get("mapx").toString()) : null)
                    .mapy(item.get("mapy") != null ? Double.parseDouble(item.get("mapy").toString()) : null)
                    .build();
            resultList.add(dto);
        }

        return resultList;
    }
}
