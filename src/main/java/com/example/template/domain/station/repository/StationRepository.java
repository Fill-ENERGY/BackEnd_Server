package com.example.template.domain.station.repository;

import com.example.template.domain.station.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByNameIsAndLatitudeIsAndLongitudeIs(String name, double latitude, double longitude);
    List<Station> findAllByOrderByScoreDesc();
}
