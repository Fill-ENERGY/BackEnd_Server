package com.example.template.domain.member.dto;

import com.example.template.domain.member.entity.GenderType;
import com.example.template.domain.member.entity.MemberType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProfileRequestDTO {
    @Getter
    @Builder
    public static class UpdateProfileDTO{
        private String nickname;
        private String phone;
        private LocalDate birth;
        private GenderType gender;
        private MemberType memberType;
    }

}
