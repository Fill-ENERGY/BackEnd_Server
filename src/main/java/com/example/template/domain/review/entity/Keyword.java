package com.example.template.domain.review.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Keyword {

    CHARGING_SPEED("충전 시간이 빨라요"),
    ACCESSIBILITY("충전소 접근이 편리해요"),
    WAITING_AREA("대기 장소가 편안해요"),
    CHARGER_MANAGEMENT("충전기 관리가 잘 되었어요"),
    FACILITY_SUPPORT("시설의 도움을 받았어요"),
    REVISIT_INTENTION("재방문 의사가 있어요");

    private final String description;
}
