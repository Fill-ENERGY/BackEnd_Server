package com.example.template.domain.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportType {
    SPAM_ADVERTISE("스팸 및 광고"),
    VIOLENCE("폭력 또는 혐오적 콘텐츠"),
    HARASSMENT("괴롭힘 또는 따돌림"),
    OBSCENE("음란물 또는 외설적 콘텐츠"),
    FRAUD("허위 정보 또는 사기"),
    PERSONAL_INFORMATION("개인 정보 노출"),
    OTHER("기타")
    ;

    private final String description;
}
