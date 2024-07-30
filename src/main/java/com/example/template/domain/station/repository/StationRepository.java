package com.example.template.domain.station.repository;

import com.example.template.domain.station.entity.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByNameIsAndLatitudeIsAndLongitudeIs(String name, double latitude, double longitude);

    List<Station> findAllByOrderByScoreDesc();

    @Query(value = "SELECT s1.* FROM Station s1 JOIN (SELECT s2.station_id, (6371 * acos(cos(radians(:latitude)) * cos(radians(s2.latitude)) * cos(radians(s2.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.latitude)))) AS distance FROM Station s2) AS stations ON s1.station_id = stations.station_id WHERE stations.distance > (SELECT (6371 * acos(cos(radians(:latitude)) * cos(radians(s2.latitude)) * cos(radians(s2.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.latitude)))) AS distance FROM Station s2 WHERE s2.station_id = :lastId) ORDER BY stations.distance ASC LIMIT :offset",
            nativeQuery = true)
    Slice<Station> findAllOrderByDistanceFromId(@Param("latitude") double latitude, @Param("longitude") double longitude, Long lastId, int offset);

    @Query(value = "SELECT s1.* FROM Station s1 JOIN (SELECT s2.station_id, (6371 * acos(cos(radians(:latitude)) * cos(radians(s2.latitude)) * cos(radians(s2.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.latitude)))) AS distance FROM Station s2) AS stations ON s1.station_id = stations.station_id ORDER BY stations.distance ASC LIMIT :offset",
            nativeQuery = true)
    Slice<Station> findAllByOrderByDistance(@Param("latitude") double latitude, @Param("longitude") double longitude, int offset);
}
