package com.example.backend.repository;

import com.example.backend.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    @Query("SELECT s FROM Spot s ORDER BY s.receive DESC LIMIT 10")
    List<Spot> findTop10Sopt();
}
