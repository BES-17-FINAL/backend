// SearchService.java
package com.example.backend.service;

import com.example.backend.dto.TourAPIDetailResponse;
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
        String apiPath;

        // 지역 코드가 있으면 지역기반 검색, 없으면 키워드 기반 검색
        if (areaCode != null) {
            apiPath = "/B551011/KorService2/areaBasedList2";
        } else {
            apiPath = "/B551011/KorService2/searchKeyword2";
        }

        Map<String, Object> searchJson = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.scheme("https")
                            .host("apis.data.go.kr")
                            .path(apiPath)
                            .queryParam("ServiceKey", secretKey)
                            .queryParam("MobileOS", "WEB")
                            .queryParam("MobileApp", "TRAVELHUB")
                            .queryParam("_type", "json")
                            .queryParam("numOfRows", 20)
                            .queryParam("pageNo", 1);

                    if (contentTypeId != null) uriBuilder.queryParam("contentTypeId", contentTypeId);
                    if (areaCode != null) uriBuilder.queryParam("areaCode", areaCode);

                    // 키워드 기반 검색일 때
                    if (areaCode == null) uriBuilder.queryParam("keyword", keyword);

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
                    .id(item.get("contentid") != null ? Long.parseLong(item.get("contentid").toString()) : null) // ← 추가
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

    // 상세조회 메서드 추가 -> 검색 결과에서 세부 정보 확인
    public TourAPIDetailResponse getSpotDetail(Long contentId, Integer contentTypeId) {
        // detailCommon 호출
        Map<String, Object> commonJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/B551011/KorService2/detailCommon2")
                        .queryParam("ServiceKey", secretKey)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "TRAVELHUB")
                        .queryParam("_type", "json")
                        .queryParam("contentId", contentId)
                        .queryParam("contentTypeId", contentTypeId)
                        .queryParam("overviewYN", "Y")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // detailIntro 호출
        Map<String, Object> introJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/B551011/KorService2/detailIntro2")
                        .queryParam("ServiceKey", secretKey)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "TRAVELHUB")
                        .queryParam("_type", "json")
                        .queryParam("contentId", contentId)
                        .queryParam("contentTypeId", contentTypeId)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // detailCommon 파싱
        Map<String, Object> response = (Map<String, Object>) commonJson.get("response");
        Map<String, Object> body = (Map<String, Object>) response.get("body");
        Map<String, Object> items = (Map<String, Object>) body.get("items");
        Object itemObj = items.get("item");
        Map<String, Object> commonItem;
        if (itemObj instanceof List) {
            commonItem = ((List<Map<String, Object>>) itemObj).get(0);
        } else {
            commonItem = (Map<String, Object>) itemObj;
        }

        // detailIntro 파싱
        Map<String, Object> introResponse = (Map<String, Object>) introJson.get("response");
        Map<String, Object> introBody = (Map<String, Object>) introResponse.get("body");
        Map<String, Object> introItems = (Map<String, Object>) introBody.get("items");
        Object introObj = introItems.get("item");
        Map<String, Object> introItem;
        if (introObj instanceof List) {
            introItem = ((List<Map<String, Object>>) introObj).get(0);
        } else if (introObj instanceof Map) {
            introItem = (Map<String, Object>) introObj;
        } else {
            introItem = new HashMap<>();
        }

        return TourAPIDetailResponse.builder()
                .title((String) commonItem.get("title"))
                .address((String) commonItem.get("addr1"))
                .overview((String) commonItem.get("overview"))
                .firstImage((String) commonItem.get("firstimage"))
                .mapx(commonItem.get("mapx") != null ? Double.parseDouble(commonItem.get("mapx").toString()) : null)
                .mapy(commonItem.get("mapy") != null ? Double.parseDouble(commonItem.get("mapy").toString()) : null)
                .info((String) introItem.get("content")) // detailIntro에서 가져올 추가 정보
                .build();
    }
    }