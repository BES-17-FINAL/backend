package com.example.backend.security;

import com.example.backend.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtFilter extends GenericFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 🔹 모든 요청에 CORS 헤더 추가
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type");

        // 🔹 Preflight OPTIONS는 바로 통과
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 🔹 API 요청만 JWT 처리
        String path = httpRequest.getRequestURI();
        if (path.startsWith("/api/")) {
            String header = httpRequest.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (jwtUtil.validateToken(token)) {
                    String email = jwtUtil.extractEmail(token);
                    userRepository.findByEmail(email).ifPresent(user -> {
                        // 권한은 null로 두고 인증 정보만 설정
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(user, null, null);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
                }
            }
        }

        chain.doFilter(request, response);
    }
}
