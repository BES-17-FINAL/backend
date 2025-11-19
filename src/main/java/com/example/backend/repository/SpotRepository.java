package com.example.backend.repository;

import com.example.backend.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    @Query(value = """
    SELECT
        s.spot_id AS "spot_id",
        s.api_spot_id AS "api_spot_id",
        r.avg_rating AS "receive",
        s.type AS "type"
    FROM spot s
    JOIN (
        SELECT spot_id, AVG(rating) AS avg_rating
        FROM spot_review
        GROUP BY spot_id
    ) r ON s.spot_id = r.spot_id
    ORDER BY r.avg_rating DESC
    LIMIT 10
""", nativeQuery = true)

    List<Spot> findTop10Sopt();

    Spot findByapiSpotId(Long contentId);
}
