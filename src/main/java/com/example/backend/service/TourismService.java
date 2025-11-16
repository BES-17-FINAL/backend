package com.example.backend.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class TourismService {

    @Value("${tourism.serviceKey}")
    private String serviceKey;

    private final WebClient webClient = WebClient.create();

    public Map<String, Object> getAreaBasedList(String contentTypeId, String numOfRows, String pageNo) {
        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/B551011/KorService2/areaBasedList2")
                        .queryParam("ServiceKey", serviceKey)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "TEST")
                        .queryParam("_type", "json")
                        .queryParam("contentTypeId", contentTypeId)
                        .queryParam("numOfRows", numOfRows)
                        .queryParam("pageNo", pageNo)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response;
    }
}