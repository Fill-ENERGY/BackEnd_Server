package com.example.template.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileRequestDTO {
    //추후 프로필에 관한 내용이 수정 될 수 있음
    @Getter
    @Builder
    public static class UpdateProfileDTO{
        private String nickname;

        @JsonCreator
        public UpdateProfileDTO(@JsonProperty("nickname") String nickname) {
            this.nickname = nickname;
        }
    }

}
