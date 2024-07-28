package com.example.template.domain.station.service.impl;

import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.enums.SortType;
import com.example.template.domain.station.exception.StationErrorCode;
import com.example.template.domain.station.exception.StationException;
import com.example.template.domain.station.repository.StationRepository;
import com.example.template.domain.station.service.StationQueryService;
import com.example.template.domain.station.util.StationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationQueryServiceImpl implements StationQueryService {

    private final StationRepository stationRepository;

    @Override
    public List<Station> getStations(String query, double latitude, double longitude) {
        List<Station> stations;
        if (query.equals(SortType.DISTANCE.toString())) {
            List<Station> found = stationRepository.findAll();
            stations = found.stream().sorted(Comparator.comparingDouble(value -> StationUtil.getDistance(value.getLatitude(), value.getLongitude(), latitude, longitude))).toList();
        }
        else if (query.equals(SortType.SCORE.toString())) {
            stations = stationRepository.findAllByOrderByScoreDesc();
        }
        else if (query.equals(SortType.LOCATION.toString())) {
            stations = stationRepository.findAll();
        }
        else {
            throw new StationException(StationErrorCode.QUERY_BAD_REQUEST);
        }
        return stations;
    }

    @Override
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @Override
    public Station getStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new StationException(StationErrorCode.NOT_FOUND));
    }

    @Override
    public List<Station> getFavoriteStation(Principal principal) {
        // TODO: 유저 구현 완료 후 구현
        return List.of();
    }
}
