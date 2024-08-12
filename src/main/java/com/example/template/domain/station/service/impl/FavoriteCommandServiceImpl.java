package com.example.template.domain.station.service.impl;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.entity.Favorite;
import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.exception.StationErrorCode;
import com.example.template.domain.station.exception.StationException;
import com.example.template.domain.station.repository.FavoriteRepository;
import com.example.template.domain.station.repository.StationRepository;
import com.example.template.domain.station.service.FavoriteCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteCommandServiceImpl implements FavoriteCommandService {

    private final StationRepository stationRepository;
    private final FavoriteRepository favoriteRepository;

    @Override
    public boolean addOrRemoveFavorite(Member member, Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(() -> new StationException(StationErrorCode.NOT_FOUND));
        favoriteRepository.findByMemberIsAndStationIs(member, station).ifPresentOrElse(
                favoriteRepository::delete,
                () ->  favoriteRepository.save(Favorite.builder()
                        .member(member)
                        .station(station)
                        .build())
        );
        return true;
    }
}
