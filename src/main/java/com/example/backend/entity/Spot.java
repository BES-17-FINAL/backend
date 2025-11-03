package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Spot")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spot_id")
    private Long id;

    @Column(name = "api_spot_id", nullable = false, unique = true)
    private Long apiSpotId;

    private float receive;

    private SpotType type;
}
