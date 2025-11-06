package com.example.backend.repository;

import com.example.backend.entity.AttractionReview;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AttractionReviewRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(AttractionReview review) {
        String sql = "INSERT INTO attraction_review (user_id, attraction_id, rating, comment) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                review.getUserId(),
                review.getAttractionId(),
                review.getRating(),
                review.getComment());
    }

    public List<AttractionReview> findByAttractionId(Long attractionId) {
        String sql = "SELECT * FROM attraction_review WHERE attraction_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AttractionReview r = new AttractionReview();
            r.setId(rs.getLong("id"));
            r.setUserId(rs.getLong("user_id"));
            r.setAttractionId(rs.getLong("attraction_id"));
            r.setRating(rs.getInt("rating"));
            r.setComment(rs.getString("comment"));
            r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return r;
        }, attractionId);
    }

    public Double getAverageRating(Long attractionId) {
        String sql = "SELECT AVG(rating) FROM attraction_review WHERE attraction_id = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, attractionId);
    }
}