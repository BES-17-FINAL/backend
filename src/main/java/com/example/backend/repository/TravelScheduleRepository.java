package com.example.backend.repository;

import com.example.backend.entity.TravelSchedule;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface TravelScheduleRepository extends JpaRepository<TravelSchedule, Long> {

    //특정 사용자의 모든 일정 날짜 시간 기준으로 정렬
    List<TravelSchedule> findAllByUserOrderByStartDateAscStartTimeAsc(User user);

    //특정 기간 조회
    List<TravelSchedule> findAllByUserAndStartDateBetweenOrderByStartDateAscStartTimeAsc(
            User user,
            LocalDate startInclusive,
            LocalDate endInclusive
    );

    boolean existsByIdAndUser(Long id, User user);
}

