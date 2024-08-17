package com.example.template.domain.station.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.entity.Station;

import java.util.List;

public interface FavoriteQueryService {
    List<Station> getFavoritesByMember(Member member, String query, double latitude, double longitude, Long lastId, int offset);
    boolean isFavorite(Member member, Station station);
}
