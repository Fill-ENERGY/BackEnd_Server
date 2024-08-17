package com.example.template.domain.station.service.impl;

import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.enums.SortType;
import com.example.template.domain.station.exception.StationErrorCode;
import com.example.template.domain.station.exception.StationException;
import com.example.template.domain.station.repository.StationRepository;
import com.example.template.domain.station.service.StationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationQueryServiceImpl implements StationQueryService {

    private final StationRepository stationRepository;

    @Override
    public List<Station> getStations(String query, Long lastId, int offset, double latitude, double longitude) {
        List<Station> stations;
        if (query.equalsIgnoreCase(SortType.DISTANCE.toString())) {
            stations = lastId.equals(0L) ? stationRepository.findAllByOrderByDistance(latitude, longitude, offset)
                    : stationRepository.findAllOrderByDistanceFromId(latitude, longitude, lastId, offset);
        }
        else if (query.equalsIgnoreCase(SortType.SCORE.toString())) {
            Pageable pageable = PageRequest.of(0, offset);
                stations = lastId.equals(0L) ? stationRepository.findAllByOrderByScoreDesc(pageable).getContent() :
                        stationRepository.findAllByOrderByScoreDescFromId(lastId, offset);

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

}
