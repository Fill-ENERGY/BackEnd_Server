package com.example.template.domain.station.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id", nullable = false)
    private Long id;

    private String name;    // 이름

    private String address; // 도로명

    private double latitude;    // 위도

    private double longitude;    // 경도

    private double score;   // 평점

    @Column(name = "weekday_open")
    private String weekdayOpen;     // 평일 운영 시작 시각

    @Column(name = "weekday_close")
    private String weekdayClose;    // 평일 운영 종료 시각

    @Column(name = "saturday_open")
    private String saturdayOpen;    // 토요일 운영 시작 시각

    @Column(name = "saturday_close")
    private String saturdayClose;   // 토요일 운영 종료 시각

    @Column(name = "holiday_open")
    private String holidayOpen;     // 공휴일 운영 시작 시각

    @Column(name = "holiday_close")
    private String holidayClose;    // 공휴일 운영 종료 시각

    @Column(name = "concurrent_usage_count")
    private String concurrentUsageCount;    // 동시사용가능대수

    @Column(name = "air_injection_available")
    private boolean airInjectionAvailable;  // 공기주입가능여부

    @Column(name = "phone_charging_available")
    private boolean phoneChargingAvailable;  // 휴대전화 충전가능여부

    @Column(name = "institution_name")
    private String institutionName;  // 관리기관명

    @Column(name = "institution_phone")
    private String institutionPhone;  // 관리기관 전화번호
}
