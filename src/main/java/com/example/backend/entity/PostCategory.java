package com.example.backend.entity;

import lombok.Getter;

@Getter
public enum PostCategory {

    CHAT("잡담"),
    QUESTION("질문"),
    TIP("꿀팁");

    private final String description;

    PostCategory(String description) {
        this.description = description;
    }

}
