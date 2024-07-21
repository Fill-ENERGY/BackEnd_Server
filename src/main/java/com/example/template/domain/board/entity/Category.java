package com.example.template.domain.board.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    DAILY("일상"),
    INQUIRY("궁금해요"),
    HELP("도와줘요"),
    WHEELCHAIR("휠체어"),
    SCOOTER("스쿠터");

    private final String description;
}
