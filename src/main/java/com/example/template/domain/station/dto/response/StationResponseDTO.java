package com.example.template.domain.station.dto.response;

import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.util.StationUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

public class StationResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StationPreviewDTO {
        private Long id;
        private String name;
        private String distance;
        private double score;
        private int scoreCount;
        private double latitude;
        private double longitude;
        // TODO: 평일 주말 구분 논의해보기
        private LocalTime openTime;
        private LocalTime closeTime;

        public static StationPreviewDTO of(Station station, double latitude, double longitude) {
            int day = LocalDate.now().getDayOfWeek().getValue();
            LocalTime openTime;
            LocalTime closeTime;
            if (day == 6) { // 토요일
                openTime = station.getSaturdayOpen();
                closeTime = station.getSaturdayClose();
            }
            else if (day == 7) { // 일요일 및 공휴일
                openTime = station.getHolidayOpen();
                closeTime = station.getHolidayClose();
            }
            else {
                openTime = station.getWeekdayOpen();
                closeTime = station.getWeekdayClose();
            }
            return StationPreviewDTO.builder()
                    .id(station.getId())
                    .name(station.getName())
                    .distance(StationUtil.getDistanceWithString(station.getLatitude(), station.getLongitude(), latitude, longitude))
                    .score(station.getScore())
                    // TODO: review 구현 이후에 추가 구현
                    .scoreCount(0)
                    .latitude(station.getLatitude())
                    .longitude(station.getLongitude())
                    .openTime(openTime)
                    .closeTime(closeTime)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StationLocationDTO {
        private Long id;
        private String name;
        private double latitude;
        private double longitude;

        public static StationLocationDTO from(Station station) {
            return StationLocationDTO.builder()
                    .id(station.getId())
                    .name(station.getName())
                    .latitude(station.getLatitude())
                    .longitude(station.getLongitude())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StationInfoDTO {
        private Long id;
        private String name;
        private String distance;
        private double score;
        private int scoreCount;
        private boolean isFavorite;
        private String address;
        private String streetNumber;
        private LocalTime weekdayOpen;
        private LocalTime weekdayClose;
        private LocalTime saturdayOpen;
        private LocalTime saturdayClose;
        private LocalTime holidayOpen;
        private LocalTime holidayClose;
        private String phoneNumber;
        private int concurrentUsageCount;
        private boolean airInjectionAvailable;
        private boolean phoneChargingAvailable;

        public static StationInfoDTO from(Station station, double latitude, double longitude) {
            return StationInfoDTO.builder()
                    .id(station.getId())
                    .name(station.getName())
                    .distance(StationUtil.getDistanceWithString(station.getLatitude(), station.getLongitude(), latitude, longitude))
                    .score(station.getScore())
                    // TODO: review 구현 이후에 추가 구현
                    .scoreCount(0)
                    .address(station.getAddress())
                    .streetNumber(station.getStreetNumber())
                    .weekdayOpen(station.getWeekdayOpen())
                    .weekdayClose(station.getWeekdayClose())
                    .saturdayOpen(station.getSaturdayOpen())
                    .saturdayClose(station.getSaturdayClose())
                    .holidayOpen(station.getHolidayOpen())
                    .holidayClose(station.getHolidayClose())
                    .phoneNumber(station.getInstitutionPhone())
                    .concurrentUsageCount(station.getConcurrentUsageCount())
                    .airInjectionAvailable(station.isAirInjectionAvailable())
                    .phoneChargingAvailable(station.isPhoneChargingAvailable())
                    .build();
        }
    }
}
