package com.example.template.domain.station.dto.response;

import com.example.template.domain.station.entity.Station;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StationOpenApiResponse {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StationResultDTO {

        @JsonProperty(namespace = "FCLTYNM")
        private String FCLTYNM; // 시설 이름

        @JsonProperty(namespace = "RDNMADR")
        private String RDNMADR; // 도로명

        @JsonProperty(namespace = "LNMADR")
        private String LNMADR; // 지번

        @JsonProperty(namespace = "LATITUDE")
        private double LATITUDE; // 위도

        @JsonProperty(namespace = "LONGITUDE")
        private double LONGITUDE; // 경도

        @JsonProperty(namespace = "WEEKDAYOPEROPENHHMM")
        private String WEEKDAYOPEROPENHHMM; // 평일 운영 시작 시각

        @JsonProperty(namespace = "WEEKDAYOPERCOLSEHHMM")
        private String WEEKDAYOPERCOLSEHHMM; // 평일 운영 종료 시각

        @JsonProperty(namespace = "SATOPEROPEROPENHHMM")
        private String SATOPEROPEROPENHHMM; // 토요일 운영 시작 시각

        @JsonProperty(namespace = "SATOPERCLOSEHHMM")
        private String SATOPERCLOSEHHMM; // 토요일 운영 종료 시각

        @JsonProperty(namespace = "HOLIDAYOPEROPENHHMM")
        private String HOLIDAYOPEROPENHHMM; // 공휴일 운영 시작 시각

        @JsonProperty(namespace = "HOLIDAYCLOSEOPENHHMM")
        private String HOLIDAYCLOSEOPENHHMM; // 공휴일 운영 종료 시각

        @JsonProperty(namespace = "SMTMUSECO")
        private int SMTMUSECO; // 동시 사용 가능 대수

        @JsonProperty(namespace = "AIRINJECTORYN")
        private char AIRINJECTORYN; // 공기 주입 가능 여부

        @JsonProperty(namespace = "MOBLPHONCHRSTNYN")
        private char MOBLPHONCHRSTNYN; // 휴대전화 충전 가능 여부

        @JsonProperty(namespace = "INSTITUTIONNM")
        private String INSTITUTIONNM; // 관리 기관명

        @JsonProperty(namespace = "INSTITUTIONPHONENUMBER")
        private String INSTITUTIONPHONENUMBER; // 관리 기관 전화 번호

        public Station toStation() {
            return Station.builder()
                    .name(this.FCLTYNM)
                    .address(this.RDNMADR)
                    .streetNumber(this.LNMADR)
                    .latitude(this.LATITUDE)
                    .longitude(this.LONGITUDE)
                    .score(0.0)
                    .weekdayOpen(LocalTime.parse(this.WEEKDAYOPEROPENHHMM, FORMATTER))
                    .weekdayClose(LocalTime.parse(this.WEEKDAYOPERCOLSEHHMM, FORMATTER))
                    .saturdayOpen(LocalTime.parse(this.SATOPEROPEROPENHHMM, FORMATTER))
                    .saturdayClose(LocalTime.parse(this.SATOPERCLOSEHHMM, FORMATTER))
                    .holidayOpen(LocalTime.parse(this.HOLIDAYOPEROPENHHMM, FORMATTER))
                    .holidayClose(LocalTime.parse(this.SATOPERCLOSEHHMM, FORMATTER))
                    .concurrentUsageCount(this.SMTMUSECO)
                    .airInjectionAvailable(this.AIRINJECTORYN == 'Y')
                    .phoneChargingAvailable(this.MOBLPHONCHRSTNYN == 'Y')
                    .institutionName(this.INSTITUTIONNM)
                    .institutionPhone(this.INSTITUTIONPHONENUMBER)
                    .build();
        }
    }
}
