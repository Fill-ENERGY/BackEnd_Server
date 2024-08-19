package com.example.template.domain.station.dto.response;

import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.util.StationUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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
        private String dayOfWeek;
        private String openTime;
        private String closeTime;
        private String institutionPhone;

        public static StationPreviewDTO of(Station station, double latitude, double longitude) {
            String dayOfWeek  = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA);
            String openTime;
            String closeTime;
            if (dayOfWeek.equals("토요일")) { // 토요일
                openTime = station.getSaturdayOpen();
                closeTime = station.getSaturdayClose();
            }
            else if (dayOfWeek.equals("일요일")) { // 일요일 및 공휴일
                openTime = station.getHolidayOpen();
                closeTime = station.getHolidayClose();
                dayOfWeek = "공휴일";
            }
            else {
                openTime = station.getWeekdayOpen();
                closeTime = station.getWeekdayClose();
                dayOfWeek = "평일";
            }
            return StationPreviewDTO.builder()
                    .id(station.getId())
                    .name(station.getName())
                    .distance(StationUtil.getDistanceWithString(station.getLatitude(), station.getLongitude(), latitude, longitude))
                    .dayOfWeek(dayOfWeek)
                    .score(station.getScore())
                    .scoreCount(station.getReviews().size())
                    .latitude(station.getLatitude())
                    .longitude(station.getLongitude())
                    .openTime(openTime)
                    .closeTime(closeTime)
                    .institutionPhone(station.getInstitutionPhone())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StationPreviewListDTO {
        private List<StationPreviewDTO> stations;
        private Long lastId;
        private boolean hasNext;

        public static StationPreviewListDTO of(List<Station> stations, double latitude, double longitude, boolean hasNext, Long lastId) {
            return StationPreviewListDTO.builder()
                    .stations(stations.stream().map(station -> StationPreviewDTO.of(station, latitude, longitude)).toList())
                    .hasNext(hasNext)
                    .lastId(lastId)
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
        private double latitude;
        private double longitude;
        private boolean isFavorite;
        private String address;
        private String streetNumber;
        private String weekdayOpen;
        private String weekdayClose;
        private String saturdayOpen;
        private String saturdayClose;
        private String holidayOpen;
        private String holidayClose;
        private String phoneNumber;
        private int concurrentUsageCount;
        private boolean airInjectionAvailable;
        private boolean phoneChargingAvailable;

        public static StationInfoDTO from(Station station, double latitude, double longitude, boolean isFavorite) {
            return StationInfoDTO.builder()
                    .id(station.getId())
                    .name(station.getName())
                    .distance(StationUtil.getDistanceWithString(station.getLatitude(), station.getLongitude(), latitude, longitude))
                    .score(station.getScore())
                    .scoreCount(station.getReviews().size())
                    .latitude(station.getLatitude())
                    .longitude(station.getLongitude())
                    .isFavorite(isFavorite)
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
