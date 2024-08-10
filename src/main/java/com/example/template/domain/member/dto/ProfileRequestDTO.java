package com.example.template.domain.member.dto;

import com.example.template.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileRequestDTO {

    @Getter
    @Builder
    public static class UpdateProfileDTO{
        private String profileImg;
        private String name;
    }

}
