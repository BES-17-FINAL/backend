package com.example.backend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attraction {
    private Long id;
    private String name;
    private String description;
    private double latitude;   // 카카오맵 연동용
    private double longitude;
    private String address;
}