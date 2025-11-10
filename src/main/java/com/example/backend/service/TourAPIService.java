package com.example.backend.service;

import com.example.backend.dto.TourAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TourAPIService {

    private final WebClient webClient;

    @Value("${secret_key}")
    private String secretKey;

    /**
     * 관광지 상세 정보 통합 (detailCommon2 + detailIntro2)
     */
    private Integer apiType;
    public TourAPIResponse getSpotDetails(Long id) {

        // ✅ 1️⃣ detailCommon2 (기본정보)
        Map<String, Object> commonJson = webClient.get()
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

        Map<String, Object> commonResponse = (Map<String, Object>) commonJson.get("response");
        Map<String, Object> commonBody = (Map<String, Object>) commonResponse.get("body");
        Map<String, Object> commonItems = (Map<String, Object>) commonBody.get("items");
        List<Map<String, Object>> commonItemList = (List<Map<String, Object>>) commonItems.get("item");
        Map<String, Object> commonItem = commonItemList.get(0);

        System.out.println(commonItem);
        apiType = Integer.parseInt(commonItem.get("contenttypeid").toString());
        System.out.println(apiType);
        // ✅ 2️⃣ detailIntro2 (부가정보 - 운영시간, 휴무일 등)
        Map<String, Object> introJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/B551011/KorService2/detailIntro2")
                        .queryParam("ServiceKey", secretKey)
                        .queryParam("contentId", id)
                        .queryParam("contentTypeId", apiType)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "TRAVELHUB")
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(introJson);
        System.out.println();
        System.out.println();
        System.out.println();

        Map<String, Object> introResponse = (Map<String, Object>) introJson.get("response");
        Map<String, Object> introBody = (Map<String, Object>) introResponse.get("body");
        Map<String, Object> introItems = (Map<String, Object>) introBody.get("items");
        Map<String, Object> introItem = null;
        if (introItems != null && introItems.get("item") instanceof List) {
            introItem = ((List<Map<String, Object>>) introItems.get("item")).get(0);
        }

        // ✅ 기본 필드
        String title = (String) commonItem.get("title");
        String tel = (String) commonItem.get("tel");
        String overview = (String) commonItem.get("overview");
        String addr1 = (String) commonItem.get("addr1");
        String homepageRaw = (String) commonItem.get("homepage");

        // ✅ 이미지
        String firstImage = (String) commonItem.get("firstimage");
        String firstImage2 = (String) commonItem.get("firstimage2");

        // ✅ 좌표
        Double mapx = parseDouble(commonItem.get("mapx"));
        Double mapy = parseDouble(commonItem.get("mapy"));

        // ✅ 홈페이지 정리
        String homepage = cleanHomepage(homepageRaw);

        // ✅ 추가 정보 (intro2)
        String useTime = getString(introItem, "usetime"); // 운영시간
        String restDate = getString(introItem, "restdate"); // 쉬는날

        // ✅ DTO 빌드
        return TourAPIResponse.builder()
                .title(title)
                .apiType(apiType)
                .tel(tel)
                .homepage(homepage)
                .firstImage(firstImage)
                .firstImage2(firstImage2)
                .description(overview)
                .address(addr1)
                .mapx(mapx)
                .mapy(mapy)
                .useTime(useTime)
                .restDate(restDate)
                .build();
    }

    // ---------- 🔧 유틸 ----------
    private Double parseDouble(Object value) {
        if (value == null) return null;
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getString(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) return null;
        return map.get(key).toString();
    }

    private String cleanHomepage(String raw) {
        if (raw == null) return null;
        String decoded = raw
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"");
        Pattern pattern = Pattern.compile("href=\\\"(.*?)\\\"");
        Matcher matcher = pattern.matcher(decoded);
        if (matcher.find()) return matcher.group(1);
        return decoded.replaceAll("<[^>]*>", "").trim();
    }

    /**
     * 검색 기능: keyword로 관광지 목록 조회
     */
    public List<TourAPIResponse> searchSpots(String keyword) {

        Map<String, Object> searchJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/B551011/KorService2/searchKeyword2")
                        .queryParam("ServiceKey", secretKey)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 20)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "TRAVELHUB")
                        .queryParam("_type", "json")
                        .queryParam("keyword", keyword)
                        .build())
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
                        .queryParam("contentTypeId", item.get("contentTypeId"))
                        .queryParam("contentId", id)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "TRAVELHUB")
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
 */