package com.example.template.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberType {
    ELECTRIC_WHEELCHAIR("전동휠체어 이용자"),
    ELECTRIC_SCOOTER("전동스쿠터 이용자"),
    GUARDIAN("보호자");

    private final String description;
}
