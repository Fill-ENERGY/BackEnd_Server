package com.example.template.domain.station.controller;

import com.example.template.domain.station.dto.response.StationResponseDTO;
import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.service.StationCommandService;
import com.example.template.domain.station.service.StationQueryService;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StationController {

    private final StationCommandService stationCommandService;
    private final StationQueryService stationQueryService;

    @GetMapping("/stations")
    @Operation(summary = "충전소 전체 가져오는 API", description = "충전소 전체를 거리순, 별점순으로 가져온다.")
    @Parameters({
            @Parameter(name = "query", description = "DISTANCE: 거리순, SCORE: 별점순"),
            @Parameter(name = "lastId", description = "마지막 충전소 id"),
            @Parameter(name = "offset", description = "가져올 충전소 개수, default = 10"),
            @Parameter(name = "latitude", description = "현재 위치의 위도"),
            @Parameter(name = "longitude", description = "현재 위치의 경도")
    })
    public ApiResponse<List<StationResponseDTO.StationPreviewDTO>> getStations(@RequestParam("query") String query,
                                                                          @RequestParam("lastId") Long lastId,
                                                                          @RequestParam(value = "offset", defaultValue = "10") int offset,
                                                                          @RequestParam("latitude") double latitude,
                                                                          @RequestParam("longitude") double longitude) {
        List<Station> stations = stationQueryService.getStations(query, lastId, offset, latitude, longitude);
        return ApiResponse.onSuccess(
                stations.stream().map(station -> StationResponseDTO.StationPreviewDTO.of(station, latitude, longitude)).toList()
        );
    }



    @GetMapping("/stations/location")
    @Operation(summary = "지도에 표시할 충전소 정보 가져오기", description = "모든 충전소에 대한 위치 정보를 가져온다.")
    public ApiResponse<List<StationResponseDTO.StationLocationDTO>> getLocationOfStations() {
        return ApiResponse.onSuccess(stationQueryService.getAllStations()
                .stream()
                .map(StationResponseDTO.StationLocationDTO::from)
                .toList());
    }

    @GetMapping("/stations/{stationId}")
    @Operation(summary = "충전소 하나 가져오는 API", description = "충전소 하나에 대한 상세 정보를 가져온다.")
    @Parameters({
            @Parameter(name = "stationId", description = "찾고자 하는 충전소의 ID"),
            @Parameter(name = "latitude", description = "현재 위치의 위도"),
            @Parameter(name = "longitude", description = "현재 위치의 경도")
    })
    public ApiResponse<StationResponseDTO.StationInfoDTO> getStation(@PathVariable Long stationId,
                                                                     @RequestParam("latitude") double latitude,
                                                                     @RequestParam("longitude") double longitude) {
        Station station = stationQueryService.getStation(stationId);
        return ApiResponse.onSuccess(StationResponseDTO.StationInfoDTO.from(station, latitude, longitude));
    }


}
