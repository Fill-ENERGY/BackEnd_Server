package com.example.template.domain.member.dto;

import com.example.template.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProfileResponseDTO {

    public static ProfileDTO from(Member member){
        return ProfileDTO.builder()
                .profileImg(member.getProfileImg())
                .createdAt(member.getCreatedAt())
                .name(member.getName())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    @Getter
    @Builder
    public static class ProfileDTO{
        private String profileImg;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String name;
    }


}
