package com.example.template.domain.station.controller;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.dto.response.StationResponseDTO;
import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.service.FavoriteCommandService;
import com.example.template.domain.station.service.FavoriteQueryService;
import com.example.template.domain.station.service.StationQueryService;
import com.example.template.global.annotation.AuthenticatedMember;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StationController {

    private final StationQueryService stationQueryService;
    private final FavoriteCommandService favoriteCommandService;
    private final FavoriteQueryService favoriteQueryService;

    @GetMapping("/stations")
    @Operation(summary = "충전소 전체 가져오는 API", description = "충전소 전체를 거리순, 별점순으로 가져온다.")
    @Parameters({
            @Parameter(name = "query", description = "DISTANCE: 거리순, SCORE: 별점순"),
            @Parameter(name = "lastId", description = "마지막 충전소 id (처음 가져올 때 -> 0)"),
            @Parameter(name = "offset", description = "가져올 충전소 개수, default = 10"),
            @Parameter(name = "latitude", description = "현재 위치의 위도"),
            @Parameter(name = "longitude", description = "현재 위치의 경도")
    })
    public ApiResponse<StationResponseDTO.StationPreviewListDTO> getStations(@RequestParam("query") String query,
                                                                          @RequestParam("lastId") Long lastId,
                                                                          @RequestParam(value = "offset", defaultValue = "10") int offset,
                                                                          @RequestParam("latitude") double latitude,
                                                                          @RequestParam("longitude") double longitude) {
        return ApiResponse.onSuccess(stationQueryService.getStations(query, lastId, offset, latitude, longitude));
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
    public ApiResponse<StationResponseDTO.StationInfoDTO> getStation(@AuthenticatedMember Member member,
                                                                     @PathVariable Long stationId,
                                                                     @RequestParam("latitude") double latitude,
                                                                     @RequestParam("longitude") double longitude) {
        Station station = stationQueryService.getStation(stationId);
        return ApiResponse.onSuccess(StationResponseDTO.StationInfoDTO.from(station, latitude, longitude, favoriteQueryService.isFavorite(member, station)));
    }

    @PostMapping("/stations/{stationId}/favorite")
    @Operation(summary = "충전소 즐겨찾기", description = "로그인된 유저가 충전소를 즐겨찾기하는 API")
    public ApiResponse<Boolean> addFavorite(@AuthenticatedMember Member member,
                                            @PathVariable Long stationId) {
        return ApiResponse.onSuccess(favoriteCommandService.addOrRemoveFavorite(member, stationId));
    }

    @GetMapping("/stations/my-favorites")
    @Operation(summary = "즐겨찾기한 충전소 조회", description = "내가 즐겨찾기한 충전소 조회")
    @Parameters({
            @Parameter(name = "query", description = "DISTANCE (거리순), RECENT (최신순)"),
            @Parameter(name = "lastId", description = "마지막 충전소 번호 처음: 0"),
            @Parameter(name = "offset", description = "가져올 충전소 개수, default = 10"),
            @Parameter(name = "latitude", description = "현재 위치의 위도"),
            @Parameter(name = "longitude", description = "현재 위치의 경도")
    })
    public ApiResponse<StationResponseDTO.StationPreviewListDTO> getFavoriteStations(@AuthenticatedMember Member member,
                                                                                       @RequestParam("query") String query,
                                                                                       @RequestParam("lastId") Long lastId,
                                                                                       @RequestParam(value = "offset", defaultValue = "10") int offset,
                                                                                       @RequestParam("latitude") double latitude,
                                                                                       @RequestParam("longitude") double longitude) {
        return ApiResponse.onSuccess(favoriteQueryService.getFavoritesByMember(member, query, latitude, longitude, lastId, offset));
    }
}
