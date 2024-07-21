package com.example.template.domain.complaint.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ComplaintType {

    EQUIPMENT_FAILURE("기기 고장"),
    FACILITY_ISSUE("시설 문제"),
    OPERATIONAL_ISSUE("운영 문제"),
    OTHER("기타");

    private final String description;
}