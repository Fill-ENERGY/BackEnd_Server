package com.example.template.domain.station.repository;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.entity.Favorite;
import com.example.template.domain.station.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByMemberIsAndStationIs(Member member, Station station);
    List<Favorite> findAllByMemberIs(Member member);
    boolean existsByMemberIsAndStationIs(Member member, Station station);
}
