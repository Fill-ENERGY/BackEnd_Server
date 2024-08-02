package com.example.template.domain.member.dto;

import com.example.template.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ProfileRequestDTO {

    //프로필에 어떤 정보가 들어 갈지 확정이 안되어 임의로 넣음 (추후변경 예정)
    @Getter
    @Builder
    public static class UpdateProfileDTO{
        private String profileImg;
        private String email;
        private String name;
    }

    @Getter
    @Builder
    public static class CreateProfileDTO{
        private String profileImg;
        private String email;
        private String name;
    }

    public static Member toEntity(CreateProfileDTO createProfileDTO) {
        return Member.builder()
                .email(createProfileDTO.email)
                .profileImg(createProfileDTO.profileImg)
                .name(createProfileDTO.name)
                .build();
    }
}
