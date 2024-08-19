package com.example.template.domain.station.repository;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.entity.Favorite;
import com.example.template.domain.station.entity.Station;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByMemberIsAndStationIs(Member member, Station station);
    List<Favorite> findAllByMemberIs(Member member);
    boolean existsByMemberIsAndStationIs(Member member, Station station);

    @Query(value = "SELECT f1.* FROM favorite f1 JOIN (SELECT s.station_id, (6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s.latitude)))) AS distance FROM station s) AS favorites ON favorites.station_id = f1.station_id WHERE f1.member_id = :memberId AND favorites.distance > (SELECT (6371 * acos(cos(radians(:latitude)) * cos(radians(s2.latitude)) * cos(radians(s2.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s2.latitude)))) AS distance FROM station s2 WHERE s2.station_id = :lastId) ORDER BY favorites.distance ASC LIMIT :offset",
            nativeQuery = true)
    List<Favorite> findAllByMemberIsAndFromLastIdOrderByDistance(@Param("memberId") Long memberId, @Param("latitude") double latitude, @Param("longitude") double longitude, @Param("lastId")Long lastId, @Param("offset") int offset);

    @Query(value = "SELECT f1.* FROM favorite f1 JOIN (SELECT s.station_id, (6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(s.latitude)))) AS distance FROM station s) AS favorites ON favorites.station_id = f1.station_id WHERE f1.member_id = :memberId ORDER BY favorites.distance ASC LIMIT :offset",
            nativeQuery = true)
    List<Favorite> findAllByMemberIsOrderByDistance(@Param("memberId") Long memberId, @Param("latitude") double latitude, @Param("longitude") double longitude, @Param("offset") int offset);

    List<Favorite> findAllByMemberIsAndCreatedAtLessThanOrderByCreatedAtDesc(Member member, LocalDateTime createdAt, Pageable pageable);

    List<Favorite> findAllByMemberIsOrderByCreatedAtDesc(Member member, Pageable pageable);
}
