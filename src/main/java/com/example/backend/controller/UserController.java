package com.example.backend.controller;

import com.example.backend.dto.UpdateUserProfileRequest;
import com.example.backend.dto.UserProfileResponse;
import com.example.backend.dto.UpdateUserProfileResponse;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileResponse me(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email
    ) {
        Long resolvedId = userService.resolveUserId(userId, email);
        return userService.me(resolvedId);
    }

    @PutMapping("/edit")
    public UpdateUserProfileResponse edit(
            @Valid @RequestBody UpdateUserProfileRequest req,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @AuthenticationPrincipal(expression = "email") String email
    ) {
        Long resolvedId = userService.resolveUserId(userId, email);
        userService.edit(resolvedId, req);
        return new UpdateUserProfileResponse(true);
    }
}