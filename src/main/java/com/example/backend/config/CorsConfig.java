package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 프론트엔드 주소 허용 (개발 환경)
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173"); // Vite 기본 포트
        config.addAllowedOrigin("http://localhost:3000"); // React 기본 포트
        
        // 모든 HTTP 메서드 허용
        config.addAllowedMethod("*");
        
        // 모든 헤더 허용
        config.addAllowedHeader("*");
        
        // Authorization 헤더 허용 (JWT 토큰용)
        config.addExposedHeader("Authorization");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

