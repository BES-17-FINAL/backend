package com.example.backend.service;

import com.example.backend.dto.FestivalResponse;
import com.example.backend.entity.Festival;
import com.example.backend.repository.FestivalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class FestivalService {

    private final FestivalRepository festivalRepository;

    public FestivalService(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

    public FestivalResponse getById(Long id) {
        Festival f = festivalRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "festival_id=" + id + " not found"));
        return toResponse(f);
    }

    public List<FestivalResponse> getOngoing(LocalDate dateOrNull) {
        LocalDate base = (dateOrNull != null) ? dateOrNull : LocalDate.now(ZoneId.of("Asia/Seoul"));
        return festivalRepository.findOngoing(base).stream()
                .map(this::toResponse)
                .toList();
    }

    private FestivalResponse toResponse(Festival f) {
        return new FestivalResponse(
                f.getFestivalId(),
                f.getFestivalName(),
                f.getFestivalDescription(),
                f.getFestivalStart(),
                f.getFestivalFinish(),
                f.getLocation()
        );
    }
}