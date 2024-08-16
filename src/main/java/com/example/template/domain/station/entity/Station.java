package com.example.template.domain.station.entity;

import com.example.template.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "station")
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id", nullable = false)
    private Long id;

    private String name;    // 이름

    private String address; // 도로명

    private String streetNumber;

    private double latitude;    // 위도

    private double longitude;    // 경도

    private double score;   // 평점

    @Column(name = "weekday_open")
    private LocalTime weekdayOpen;     // 평일 운영 시작 시각

    @Column(name = "weekday_close")
    private LocalTime weekdayClose;    // 평일 운영 종료 시각

    @Column(name = "saturday_open")
    private LocalTime saturdayOpen;    // 토요일 운영 시작 시각

    @Column(name = "saturday_close")
    private LocalTime saturdayClose;   // 토요일 운영 종료 시각

    @Column(name = "holiday_open")
    private LocalTime holidayOpen;     // 공휴일 운영 시작 시각

    @Column(name = "holiday_close")
    private LocalTime holidayClose;    // 공휴일 운영 종료 시각

    @Column(name = "concurrent_usage_count")
    private Integer concurrentUsageCount;    // 동시사용가능대수

    @Column(name = "air_injection_available")
    private boolean airInjectionAvailable;  // 공기주입가능여부

    @Column(name = "phone_charging_available")
    private boolean phoneChargingAvailable;  // 휴대전화 충전가능여부

    @Column(name = "institution_name")
    private String institutionName;  // 관리기관명

    @Column(name = "institution_phone")
    private String institutionPhone;  // 관리기관 전화번호

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    public void update(Station station) {
        this.name = station.getName();
        this.address = station.getAddress();
        this.streetNumber = station.getStreetNumber();
        this.weekdayOpen = station.getWeekdayOpen();
        this.weekdayClose = station.getWeekdayClose();
        this.saturdayOpen = station.getSaturdayOpen();
        this.saturdayClose = station.getSaturdayClose();
        this.holidayOpen = station.getHolidayOpen();
        this.holidayClose = station.getHolidayClose();
        this.concurrentUsageCount = station.getConcurrentUsageCount();
        this.airInjectionAvailable = station.isAirInjectionAvailable();
        this.phoneChargingAvailable = station.isPhoneChargingAvailable();
        this.institutionName = station.institutionName;
        this.institutionPhone = station.institutionPhone;
    }

    public void updateScore() {
        if (reviews != null && !reviews.isEmpty()) {
            double totalScore = 0.0;
            for (Review review : reviews) {
                totalScore += review.getScore();
            }
            this.score = totalScore / reviews.size();
        }
        else {
            this.score = 0.0;
        }
    }
}
