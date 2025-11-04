package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public String processOAuthLogin(OAuth2User oAuthUser, String provider) {
        Map<String, Object> attributes = oAuthUser.getAttributes();

        String email;
        String name;

        switch (provider.toLowerCase()) {
            case "google" -> {
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
            }
            case "github" -> {
                email = (String) attributes.get("email");
                name = (String) attributes.get("login");
            }
            case "kakao" -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                if (kakaoAccount == null) throw new RuntimeException("Kakao 계정 정보를 가져올 수 없습니다.");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile == null) throw new RuntimeException("Kakao 프로필 정보를 가져올 수 없습니다.");
                email = (String) kakaoAccount.get("email");
                name = (String) profile.get("nickname");
            }
            default -> throw new RuntimeException("지원하지 않는 OAuth 제공자: " + provider);
        }

        if (email == null) throw new RuntimeException("OAuth 로그인 중 이메일 정보를 가져올 수 없습니다.");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .nickname(name)
                        .userName(name)
                        .loginType(provider.toUpperCase())
                        .build()));

        return jwtUtil.generateToken(email);
    }
}
