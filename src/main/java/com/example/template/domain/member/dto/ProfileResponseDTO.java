package com.example.template.domain.member.dto;

import com.example.template.domain.member.entity.GenderType;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.entity.MemberType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProfileResponseDTO {


    @Getter
    @Builder
    public static class MyProfileDTO{
        private String profileImg;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String name;
        private String nickname;
        private GenderType gender;
        private LocalDate birth;
        private MemberType memberType;

        public static MyProfileDTO from(Member member) {
            return MyProfileDTO.builder()
                    .gender(member.getGender())
                    .birth(member.getBirth())
                    .memberType(member.getMemberType())
                    .profileImg(member.getProfileImg())
                    .createdAt(member.getCreatedAt())
                    .name(member.getName())
                    .updatedAt(member.getUpdatedAt())
                    .nickname(member.getNickname())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ProfileDTO {
        private String profileImg;
        private String name;
        private String nickname;

        public static ProfileDTO from(Member member) {
            return ProfileDTO.builder()
                    .profileImg(member.getProfileImg())
                    .name(member.getName())
                    .nickname(member.getNickname())
                    .build();
        }
    }
}
