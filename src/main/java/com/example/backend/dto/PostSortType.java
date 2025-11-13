package com.example.backend.dto;

public enum PostSortType {
    LATEST,
    OLDEST,
    MOST_VIEWS,
    MOST_COMMENTS,
    MOST_LIKES;

    public static PostSortType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return LATEST;
        }
        try {
            return PostSortType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return LATEST;
        }
    }
}

