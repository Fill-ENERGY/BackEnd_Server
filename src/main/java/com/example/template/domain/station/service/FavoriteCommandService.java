package com.example.template.domain.station.service;

import com.example.template.domain.member.entity.Member;

public interface FavoriteCommandService {
    boolean addOrRemoveFavorite(Member member, Long stationId);
}
