package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUserProfileRequest {
    @NotBlank private String nickname;
    @NotBlank private String user_name;
    private String profile_img;
}