package com.example.template.domain.station.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.dto.response.StationResponseDTO;
import com.example.template.domain.station.entity.Station;

import java.security.Principal;
import java.util.List;

public interface StationQueryService {
    StationResponseDTO.StationPreviewListDTO getStations(String query, Long lastId, int offset, double latitude, double longitude);
    Station getStation(Long stationId);
    List<Station> getAllStations();
}
