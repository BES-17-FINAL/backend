package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "festival", indexes = {
        @Index(name = "idx_festival_name", columnList = "festival_name"),
        @Index(name = "idx_festival_period", columnList = "festival_start,festival_finish")
})
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "festival_id")
    private Long festivalId;

    @Column(name = "festival_name", nullable = false, length = 200)
    private String festivalName;

    @Column(name = "festival_description", columnDefinition = "text")
    private String festivalDescription;

    @Column(name = "festival_start", nullable = false)
    private LocalDate festivalStart;

    @Column(name = "festival_finish", nullable = false)
    private LocalDate festivalFinish;

    @Column(name = "location", length = 255)
    private String location;
}