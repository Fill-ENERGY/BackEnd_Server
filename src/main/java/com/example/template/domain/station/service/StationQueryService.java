package com.example.template.domain.station.service;

import com.example.template.domain.station.entity.Station;

import java.security.Principal;
import java.util.List;

public interface StationQueryService {
    List<Station> getStations(String query, Long lastId, int offset, double latitude, double longitude);
    List<Station> getFavoriteStation(Principal principal);
    Station getStation(Long stationId);
    List<Station> getAllStations();
}
