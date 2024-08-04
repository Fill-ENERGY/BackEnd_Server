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

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationQueryServiceImpl implements StationQueryService {

    private final StationRepository stationRepository;

    @Override
    public List<Station> getStations(String query, Long lastId, int offset, double latitude, double longitude) {
        List<Station> stations;
        if (query.equals(SortType.DISTANCE.toString())) {
            stations = lastId.equals(0L) ? stationRepository.findAllByOrderByDistance(latitude, longitude, offset).getContent()
                    : stationRepository.findAllOrderByDistanceFromId(latitude, longitude, lastId, offset).getContent();
        }
        else if (query.equals(SortType.SCORE.toString())) {
            stations = scorePagination(stationRepository.findAllByOrderByScoreDesc(), lastId, offset);

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

    /**
     * 별점순으로 정렬된 List를 무한스크롤 형식으로 페이지네이션하는 함수
     * @param stations 별점순으로 정렬된 리스트
     * @param lastId 마지막 충전소의 Id
     * @param offset 가져올 개수
     */
    private List<Station> scorePagination(List<Station> stations, Long lastId, int offset) {
        int start = 0;
        int end = offset;
        if (!lastId.equals(0L)) {
            Optional<Station> found = stations.stream().filter(station -> station.getId().equals(lastId)).findFirst();
            if (found.isPresent()) {
                start = stations.indexOf(found.get()) + 1;
                end = start + offset;
            }
            else {
                throw new StationException(StationErrorCode.NOT_FOUND);
            }
        }
        if (end >= stations.size()) {
            end = stations.size();
        }
        return stations.subList(start, end);
    }
}
