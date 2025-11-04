package com.example.backend.controller;

import com.example.backend.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;


    @GetMapping("/oauth/success")
    public String oauthSuccess(Authentication authentication,
                               @RequestParam(name = "provider", defaultValue = "unknown") String provider) {
        OAuth2User oAuthUser = (OAuth2User) authentication.getPrincipal();
        String jwt = oAuthService.processOAuthLogin(oAuthUser, provider);
        return "OAuth 로그인 성공! JWT 토큰: " + jwt;
    }
}