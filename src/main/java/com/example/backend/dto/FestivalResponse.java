package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record FestivalResponse(
        @JsonProperty("festival_id") Long festival_id,
        @JsonProperty("festival_name") String festival_name,
        @JsonProperty("festival_description") String festival_description,
        @JsonProperty("festival_start") LocalDate festival_start,
        @JsonProperty("festival_finish") LocalDate festival_finish,
        @JsonProperty("location") String location
) {}