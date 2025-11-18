package com.example.backend.config;

import com.example.backend.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // 🔹 비밀번호 암호화용 Bean
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔹 AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 🔹 글로벌 CORS 설정 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ 허용할 오리진 (React 개발 서버 주소)
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        // ✅ 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // ✅ 허용할 요청 헤더
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // ✅ 쿠키 포함 여부 (여기서는 false, JWT는 헤더 사용)
        config.setAllowCredentials(false);

        // URL 패턴과 CORS 설정 연결
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // 🔹 SecurityFilterChain 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ 글로벌 CORS 적용
                .cors(cors -> {})

                // ✅ CSRF 비활성화 (REST API용)
                .csrf(csrf -> csrf.disable())

                // ✅ 세션 없이 Stateless 설정 (JWT 인증)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ 요청별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 요청은 Preflight 요청이므로 항상 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // API 요청: JWT 없어도 접근 가능
                        .requestMatchers("/api/**").permitAll()
                        // 웹 로그인/OAuth2 관련 URL 허용
                        .requestMatchers("/auth/**", "/oauth2/**", "/login/**").permitAll()
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // ✅ JWT 필터 등록 (API 요청만 처리)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // ✅ OAuth2 로그인 설정 (웹 브라우저 전용)
                .oauth2Login(oauth -> oauth
                        .loginPage("/auth/login")
                        .defaultSuccessUrl("/oauth/success", true)
                );

        return http.build();
    }
}
