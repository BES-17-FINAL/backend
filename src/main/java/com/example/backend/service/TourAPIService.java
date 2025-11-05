package com.example.backend.service;

import com.example.backend.dto.TourAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

    public TourAPIResponse getSpotDetails(Long id) {
        // ✅ 외부 API 호출
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

        // ✅ 응답 파싱
        Map<String, Object> response = (Map<String, Object>) spotJson.get("response");
        Map<String, Object> body = (Map<String, Object>) response.get("body");
        Map<String, Object> items = (Map<String, Object>) body.get("items");
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");
        Map<String, Object> item = itemList.get(0);

        // ✅ 기본 필드 추출
        String title = (String) item.get("title");
        String tel = (String) item.get("tel");
        String overview = (String) item.get("overview");
        String addr1 = (String) item.get("addr1");
        String homepageRaw = (String) item.get("homepage");

        // ✅ 이미지 필드
        String firstImage = (String) item.get("firstimage");
        String firstImage2 = (String) item.get("firstimage2");

        // ✅ 좌표 (문자열 → double 변환)
        Double mapx = parseDouble(item.get("mapx"));
        Double mapy = parseDouble(item.get("mapy"));

        // ✅ homepage 가공 (HTML 태그 제거 or URL 추출)
        String homepage = cleanHomepage(homepageRaw);

        // ✅ DTO 빌드
        return TourAPIResponse.builder()
                .title(title)
                .apiType(parseInt(item.get("contenttypeid")))
                .tel(tel)
                .homepage(homepage)
                .firstImage(firstImage)
                .firstImage2(firstImage2)
                .description(overview)
                .address(addr1)
                .mapx(mapx)
                .mapy(mapy)
                .build();
    }

    // 🔹 숫자 파싱 (null-safe)
    private Double parseDouble(Object value) {
        if (value == null) return null;
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInt(Object value) {
        if (value == null) return 0;
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // 🔹 homepage HTML 정리
    private String cleanHomepage(String raw) {
        if (raw == null) return null;

        // &lt; &gt; &quot; 등 HTML 엔티티 복원
        String decoded = raw
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"");

        // href="..." 패턴 추출
        Pattern pattern = Pattern.compile("href=\\\"(.*?)\\\"");
        Matcher matcher = pattern.matcher(decoded);
        if (matcher.find()) {
            return matcher.group(1);
        }

        // 태그 제거 (혹시 몰라서)
        return decoded.replaceAll("<[^>]*>", "").trim();
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