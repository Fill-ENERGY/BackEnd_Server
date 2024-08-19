package com.example.template.domain.station.repository;

import com.example.template.domain.station.entity.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByNameIsAndLatitudeIsAndLongitudeIs(String name, double latitude, double longitude);

    @Query(value = "SELECT s1.* FROM station s1 JOIN (SELECT s2.station_id, CONCAT(LPAD(s2.score, 10, '0'), LPAD(s2.station_id, 10, '0')) as cursorValue FROM station s2) as cursorTable ON cursorTable.station_id = s1.station_id WHERE cursorValue < (SELECT CONCAT(LPAD(s2.score, 10, '0'), LPAD(s2.station_id, 10, '0')) FROM station s2 WHERE s2.station_id = :lastId) ORDER BY score DESC, station_id DESC  LIMIT :offset",
            nativeQuery = true)
    List<Station> findAllByOrderByScoreDescFromId(@Param("lastId") Long lastId, @Param("offset") int offset);

    Page<Station> findAllByOrderByScoreDesc(Pageable pageable);

    @Query(value = "SELECT s1.* FROM station s1 JOIN (SELECT s2.station_id, (6371 * acos(cos(radians(:latitude)) * cos(radians(s2.latitude)) * cos(radians(s2.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.latitude)))) AS distance FROM station s2) AS stations ON s1.station_id = stations.station_id WHERE stations.distance > (SELECT (6371 * acos(cos(radians(:latitude)) * cos(radians(s2.latitude)) * cos(radians(s2.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.latitude)))) AS distance FROM station s2 WHERE s2.station_id = :lastId) ORDER BY stations.distance ASC LIMIT :offset",
            nativeQuery = true)
    List<Station> findAllOrderByDistanceFromId(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("lastId") Long lastId, @Param("offset") int offset);

    @Query(value = "SELECT s1.* FROM station s1 JOIN (SELECT s2.station_id, (6371 * acos(cos(radians(:latitude)) * cos(radians(s2.latitude)) * cos(radians(s2.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.latitude)))) AS distance FROM station s2) AS stations ON s1.station_id = stations.station_id ORDER BY stations.distance ASC LIMIT :offset",
            nativeQuery = true)
    List<Station> findAllByOrderByDistance(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("offset") int offset);
}
