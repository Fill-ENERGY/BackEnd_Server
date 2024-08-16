package com.example.template.domain.station.service.impl;

import com.example.template.domain.station.dto.response.StationOpenApiResponse;
import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.exception.StationErrorCode;
import com.example.template.domain.station.exception.StationException;
import com.example.template.domain.station.exception.StationOpenAPIRuntimeException;
import com.example.template.domain.station.repository.StationRepository;
import com.example.template.domain.station.service.StationCommandService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StationCommandServiceImpl implements StationCommandService {

    private final StationRepository stationRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    @Value("${station.endpoint}")
    private String endpoint;

    @Override
    public void updateScore(Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(() ->
                new StationException(StationErrorCode.NOT_FOUND));
        station.updateScore();
    }

    @Override
    @Scheduled(fixedDelayString = "${station.update.interval}", initialDelay = 3600000) // 1시간마다 갱신
    public void updateStations() {
        getStationsWithAPI()
                .flatMapMany(this::getStations)
                .doOnError(e -> log.error("에러 발생: " + e.getMessage()))
                .doOnComplete(() -> log.info("충전소 정보를 갱신하였습니다."))
                .subscribe();
    }

    /**
     * 충전소 Open Api를 이용하여 갱신하기
     */
    private Mono<Integer> getStationsWithAPI() {
        return webClient
                .get()
                .uri(endpoint + "/1/1")
                .retrieve()
                .bodyToMono(String.class)
                .map(this::getCountOfStations);
    }

    /**
     * 전체 충전소 정보를 가져오기
     * @param count 미리 알아낸 총 충전소의 개수
     */
    private Flux<Integer> getStations(int count) {
        // 한 번에 5개씩만 요청 가능
        return Flux.range(1, count / 5).flatMap(
                index -> webClient
                        .get()
                        .uri(endpoint + "/" + (5 * index + 1) + "/" + (5 * (index + 1)))
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(this::updateStations)
                        .doOnError(e -> {
                            log.error(e.getMessage());
                            throw new StationOpenAPIRuntimeException("충전소 정보 가져오는 도중 오류");
                        })
        );

    }


    private Integer getCountOfStations(String jsonResponse) {
        try {
            JsonNode countNode = objectMapper.readTree(jsonResponse).path("tbElecWheelChrCharge").path("list_total_count");
            return countNode.asInt();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new StationOpenAPIRuntimeException("충전소 개수 가져오는 도중 오류 발생");
        }
    }

    private Integer updateStations(String jsonResponse) {
        try {
            JsonNode rows = objectMapper.readTree(jsonResponse).path("tbElecWheelChrCharge").path("row");

            if (rows.isArray()) {
                return StreamSupport.stream(rows.spliterator(), false)
                        .map(data -> objectMapper.convertValue(data, StationOpenApiResponse.StationResultDTO.class))
                        .map(this::updateStation)
                        .reduce(0, Integer::sum);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new StationOpenAPIRuntimeException("충전소 전체 업데이트 도중 오류 발생");
        }
        return 0;
    }


    private Integer updateStation(StationOpenApiResponse.StationResultDTO stationDTO) {
        try {
            stationRepository.findByNameIsAndLatitudeIsAndLongitudeIs(stationDTO.getFCLTYNM(), stationDTO.getLATITUDE(), stationDTO.getLONGITUDE()).ifPresentOrElse(
                    found -> found.update(stationDTO.toStation()),
                    () -> stationRepository.save(stationDTO.toStation())
            );
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0;
        }
    }

}
