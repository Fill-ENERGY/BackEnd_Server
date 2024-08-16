package com.example.template.domain.station.service.impl;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.entity.Favorite;
import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.repository.FavoriteRepository;
import com.example.template.domain.station.service.FavoriteQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteQueryServiceImpl implements FavoriteQueryService {

    private final FavoriteRepository favoriteRepository;

    @Override
    public List<Station> getFavoritesByMember(Member member) {
        return favoriteRepository.findAllByMemberIs(member)
                .stream()
                .map(Favorite::getStation)
                .toList();
    }

    @Override
    public boolean isFavorite(Member member, Station station) {
        return favoriteRepository.existsByMemberIsAndStationIs(member, station);
    }
}
