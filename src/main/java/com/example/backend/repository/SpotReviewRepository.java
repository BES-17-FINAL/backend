package com.example.backend.repository;

import com.example.backend.entity.SpotReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotReviewRepository extends JpaRepository<SpotReview, Long> {

}