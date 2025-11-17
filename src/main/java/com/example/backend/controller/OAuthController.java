package com.example.backend.controller;

import com.example.backend.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;


    @GetMapping("/oauth/success")
    public String oauthSuccess(OAuth2AuthenticationToken authToken) {
        String provider = authToken.getAuthorizedClientRegistrationId();
        OAuth2User oAuthUser = authToken.getPrincipal();
        String jwt = oAuthService.processOAuthLogin(oAuthUser, provider);
        return "OAuth 로그인 성공! JWT 토큰: " + jwt;
    }

}