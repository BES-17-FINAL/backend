package com.example.backend.controller;

import com.example.backend.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/oauth/success")
    public void oauthSuccess(OAuth2AuthenticationToken authToken, HttpServletResponse response) throws IOException {

        String provider = authToken.getAuthorizedClientRegistrationId();
        OAuth2User oAuthUser = authToken.getPrincipal();

        // JWT 생성
        String jwt = oAuthService.processOAuthLogin(oAuthUser, provider);

        // 🔥 React로 JWT를 쿼리파라미터로 전달하면서 redirect
        response.sendRedirect("http://localhost:5173/oauth/callback?token=" + jwt);
    }
}
