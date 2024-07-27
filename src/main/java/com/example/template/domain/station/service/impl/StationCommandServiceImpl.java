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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    @Scheduled(fixedDelay = 3600000) // 1시간마다 갱신
    public void updateStations() {
        getStationsWithAPI();
    }

    /**
     * 충전소 Open Api를 이용하여 갱신하기
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
                        JsonNode countNode = objectMapper.readTree(jsonResponse).path("tbElecWheelChrCharge").path("list_total_count");
                        getStations(countNode.asInt());
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                },
                error -> {
                    log.error("충전소 개수 요청 도중 에러 발생");
                    log.error(error.getMessage());
                }
        );
    }

    /**
     * 전체 충전소 정보를 가져오기
     * @param count 미리 알아낸 총 충전소의 개수
     */
    private void getStations(int count) {
        // 한 번에 5개씩만 요청 가능
        Flux<String> response = Flux.range(1, count / 5).flatMap(
                index -> webClient
                            .get()
                            .uri(endpoint + "/" + (5 * index + 1) + "/" + (5 * (index + 1)))
                            .retrieve()
                            .bodyToMono(String.class)
        );

        response.subscribe(
                jsonResponse -> {
                    try {
                        JsonNode rows = objectMapper.readTree(jsonResponse).path("tbElecWheelChrCharge").path("row");

                        for (JsonNode row : rows) {
                            StationOpenApiResponse.StationResultDTO station = objectMapper.readValue(row.toString(), StationOpenApiResponse.StationResultDTO.class);
                            updateStation(station);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                },
                error -> {
                    log.error("충전소 정보 요청 도중 에러 발생");
                    log.error(error.getMessage());
                },
                () -> log.info("충전소 정보를 갱신하였습니다.")

        );

    }

    private void updateStation(StationOpenApiResponse.StationResultDTO stationDTO) {
        stationRepository.findByNameIsAndLatitudeIsAndLongitudeIs(stationDTO.getFCLTYNM(), stationDTO.getLATITUDE(), stationDTO.getLONGITUDE()).ifPresentOrElse(
                found -> found.update(stationDTO.toStation()),
                () -> stationRepository.save(stationDTO.toStation())
        );
    }

}
