package com.example.template.domain.station.service.impl;

import com.example.template.domain.station.dto.response.StationOpenApiResponse;
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
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StationCommandServiceImpl implements StationCommandService {

    private final StationRepository stationRepository;
    private final WebClient webClient;
    @Value("${station.endpoint}")
    private String endpoint;

    @Override
    @Scheduled(fixedDelay = 10000) // 10초마다 갱신
    public void updateStations() {
        getStationsWithAPI();
    }

    /**
     * 충전소 개수 가지고 오기
     */
    private void getStationsWithAPI() {
        Mono<String> response = webClient
                .get()
                .uri(endpoint + "/1/1")
                .retrieve()
                .bodyToMono(String.class);

        response.subscribe(
                jsonResponse -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode countNode = objectMapper.readTree(jsonResponse).path("tbElecWheelChrCharge").path("list_total_count");
                        getStations(countNode.asInt());
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                },
                error -> {
                    log.error("충전소 개수 요청 도중 에러 발생");
                    log.error(error.getMessage());
                },
                () -> log.info("충전소 정보를 갱신하였습니다.")
        );
    }

    private void getStations(int count) {
        // 한 번에 5개씩만 요청 가능
        for (int i = 0;  i <= count / 5; i++) {
            Mono<String> response = webClient
                    .get()
                    .uri(endpoint + "/" + (5 * i + 1) + "/" + (5 * (i + 1)))
                    .retrieve()
                    .bodyToMono(String.class);

            response.subscribe(
                    jsonResponse -> {
                        try {

                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode rows = objectMapper.readTree(jsonResponse).path("tbElecWheelChrCharge").path("row");

                            if (rows.isArray()) {
                                for (JsonNode data : rows) {
                                    StationOpenApiResponse.StationResultDTO station = objectMapper.readValue(data.toString(), StationOpenApiResponse.StationResultDTO.class);
                                    updateStation(station);
                                }
                            }
                            else {
                                StationOpenApiResponse.StationResultDTO station = objectMapper.readValue(rows.toString(), StationOpenApiResponse.StationResultDTO.class);
                                updateStation(station);
                            }
                        } catch(Exception e) {
                            log.error(e.getMessage());
                        }
                    },
                    error -> {
                        log.error("충전소 정보 갱신 도중 에러 발생");
                        log.error(error.getMessage());
                    }
            );
        }
    }

    private void updateStation(StationOpenApiResponse.StationResultDTO stationDTO) {
        stationRepository.findByNameIsAndLatitudeIsAndLongitudeIs(stationDTO.getFCLTYNM(), stationDTO.getLATITUDE(), stationDTO.getLONGITUDE()).ifPresentOrElse(
                found -> {
                    found.update(stationDTO.toStation());
                },
                () -> stationRepository.save(stationDTO.toStation())
        );
    }

}
