package com.example.backend.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CnctrRateResponse {
    private float cnctrRate;
    private LocalDate baseYmd;
}
