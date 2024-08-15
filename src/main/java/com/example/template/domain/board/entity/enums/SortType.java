package com.example.template.domain.board.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortType {
    LIKES("추천순"),
    LATEST("최신순");

    private final String description;
}