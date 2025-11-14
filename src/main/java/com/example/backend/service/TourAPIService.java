package com.example.backend.service;

import com.example.backend.dto.TourAPIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TourAPIService {

    private final WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${secret_key}")
    private String secretKey;

    private Integer apiType;

    /**
     * 관광지 상세 정보 통합 (detailCommon2 + detailIntro2)
     */
    public TourAPIResponse getSpotDetails(Long id) {

        Map<String, Object> commonItem = detailCommon(id);

        apiType = Integer.parseInt(commonItem.get("contenttypeid").toString());

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

        Map<String, Object> introResponse = (Map<String, Object>) introJson.get("response");
        Map<String, Object> introBody = (Map<String, Object>) introResponse.get("body");
        Map<String, Object> introItems = (Map<String, Object>) introBody.get("items");
        Map<String, Object> introItem = null;
        if (introItems != null && introItems.get("item") instanceof List) {
            introItem = ((List<Map<String, Object>>) introItems.get("item")).get(0);
        }

        String title = (String) commonItem.get("title");
        String tel = (String) commonItem.get("tel");
        String overview = (String) commonItem.get("overview");
        String addr1 = (String) commonItem.get("addr1");
        String homepageRaw = (String) commonItem.get("homepage");

        String firstImage = (String) commonItem.get("firstimage");
        String firstImage2 = (String) commonItem.get("firstimage2");

        Double mapx = parseDouble(commonItem.get("mapx"));
        Double mapy = parseDouble(commonItem.get("mapy"));

        String homepage = cleanHomepage(homepageRaw);

        String useTime = getString(introItem, "usetime");
        String restDate = getString(introItem, "restdate");

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

    /**
     * 키워드로 관광지 검색
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

    /**
     * ID 리스트로 관광지 조회
     */
    public List<TourAPIResponse> findSpotByIdList(List<Long> apiSpotIds) {
        return apiSpotIds.stream()
                .map(this::detailCommon)
                .filter(Objects::nonNull)
                .map(map -> {
                    map.put("address", map.remove("addr1"));
                    map.put("description", map.remove("overview"));
                    map.put("firstImage", map.remove("firstimage"));
                    map.put("firstImage2", map.remove("firstimage2"));
                    return map;
                })
                .map(map -> objectMapper.convertValue(map, TourAPIResponse.class))
                .peek(res -> res.setHomepage(cleanHomepage(res.getHomepage())))
                .toList();
    }

    /**
     * 공통 상세 조회
     */
    private Map<String, Object> detailCommon(Long id) {
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

        return commonItemList.get(0);
    }

    /**
     * 연관 관광지/통계 조회
     */
    public List<Map<String, Object>> getcnctrRate(Long id) {
        Map<String, Object> common = detailCommon(id);

        String title = (String) common.get("title");
        int lDongRegnCd = Integer.parseInt((String) common.get("lDongRegnCd"));
        int lDongSignguCd = Integer.parseInt((String) common.get("lDongSignguCd"));

        Map<String, Object> commonJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("apis.data.go.kr")
                        .path("/B551011/TatsCnctrRateService/tatsCnctrRatedList")
                        .queryParam("ServiceKey", secretKey)
                        .queryParam("MobileOS", "WEB")
                        .queryParam("MobileApp", "TRAVELHUB")
                        .queryParam("_type", "json")
                        .queryParam("numOfRows", 30)
                        .queryParam("areaCd", lDongRegnCd)
                        .queryParam("signguCd", lDongRegnCd + "" + lDongSignguCd)
                        .queryParam("tAtsNm", title)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map<String, Object> commonResponse = (Map<String, Object>) commonJson.get("response");
        Map<String, Object> commonBody = (Map<String, Object>) commonResponse.get("body");
        Object itemsObj = commonBody.get("items");
        if (!(itemsObj instanceof Map)) {
            return new ArrayList<>();
        }
        Map<String, Object> commonItems = (Map<String, Object>) commonBody.get("items");
        List<Map<String, Object>> commonItemList = (List<Map<String, Object>>) commonItems.get("item");

        return commonItemList;
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
}
