package com.example.backend.dto;

public enum PostSearchType {
    TITLE,
    CONTENT,
    TITLE_CONTENT,
    NICKNAME;

    public static PostSearchType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return TITLE_CONTENT;
        }
        try {
            return PostSearchType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return TITLE_CONTENT;
        }
    }
}

