package com.example.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.example.backend.entity.PostCategory;
@Getter
@Setter
@NoArgsConstructor
public class PostRequest {

    private String title;

    private String content;

    private PostCategory category;
}
