package com.example.template.domain.board.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HelpStatus {

    NONE("없음"),
    REQUESTED("요청 중"),
    IN_PROGRESS("연락 중"),
    RESOLVED("해결 완료");

    private final String description;
}
