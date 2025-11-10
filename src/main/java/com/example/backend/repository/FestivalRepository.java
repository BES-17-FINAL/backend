package com.example.backend.repository;

import com.example.backend.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    // 진행중: festival_start <= :d AND festival_finish >= :d
    @Query("""
           SELECT f
           FROM Festival f
           WHERE f.festivalStart <= :d
             AND f.festivalFinish >= :d
           ORDER BY f.festivalFinish ASC, f.festivalName ASC
           """)
    List<Festival> findOngoing(@Param("d") LocalDate date);
}