package com.example.template.domain.station.service.impl;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.entity.Favorite;
import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.enums.SortType;
import com.example.template.domain.station.exception.FavoriteErrorCode;
import com.example.template.domain.station.exception.FavoriteException;
import com.example.template.domain.station.exception.StationErrorCode;
import com.example.template.domain.station.exception.StationException;
import com.example.template.domain.station.repository.FavoriteRepository;
import com.example.template.domain.station.repository.StationRepository;
import com.example.template.domain.station.service.FavoriteQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteQueryServiceImpl implements FavoriteQueryService {

    private final FavoriteRepository favoriteRepository;
    private final StationRepository stationRepository;

    @Override
    public List<Station> getFavoritesByMember(Member member, String query, double latitude, double longitude, Long lastId, int offset) {
        List<Favorite> favorites;
        if (query.equalsIgnoreCase(SortType.RECENT.toString())) {
            Pageable pageable = PageRequest.of(0, offset);
            if (lastId.equals(0L)) {
                favorites = favoriteRepository.findAllByMemberIsOrderByCreatedAtDesc(member, pageable);
            }
            else {
                Station station = stationRepository.findById(lastId).orElseThrow(() -> new StationException(StationErrorCode.NOT_FOUND));
                Favorite favorite = favoriteRepository.findByMemberIsAndStationIs(member, station).orElseThrow(() -> new FavoriteException(FavoriteErrorCode.NOT_FOUND));
                favorites = favoriteRepository.findAllByMemberIsAndCreatedAtLessThanOrderByCreatedAtDesc(member, favorite.getCreatedAt(), pageable);
            }
        }
        else if (query.equalsIgnoreCase(SortType.DISTANCE.toString())) {
            favorites = lastId.equals(0L) ? favoriteRepository.findAllByMemberIsOrderByDistance(member.getId(), latitude, longitude, offset)
                    : favoriteRepository.findAllByMemberIsAndFromLastIdOrderByDistance(member.getId(), latitude, longitude, lastId, offset);
        }
        else {
            throw new FavoriteException(FavoriteErrorCode.QUERY_BAD_REQUEST);
        }
        return favorites.stream().map(Favorite::getStation).toList();
    }

    @Override
    public boolean isFavorite(Member member, Station station) {
        return favoriteRepository.existsByMemberIsAndStationIs(member, station);
    }
}
