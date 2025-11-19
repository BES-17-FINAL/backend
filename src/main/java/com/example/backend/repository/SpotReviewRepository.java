package com.example.backend.repository;

import com.example.backend.entity.SpotReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotReviewRepository extends JpaRepository<SpotReview, Long> {
    List<SpotReview> findByUser_UserId(Long userId);

}