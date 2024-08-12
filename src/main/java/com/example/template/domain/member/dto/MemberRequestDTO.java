package com.example.template.domain.member.dto;

import com.example.template.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

public class MemberRequestDTO {
    @Getter
    public static class LoginDTO {
        @NotBlank(message = "[ERROR] 이메일 입력은 필수입니다.")
        @Schema(description = "email", example = "test1234@umc.com")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "[ERROR] 이메일 형식에 맞지 않습니다.")
        String email;

        @NotBlank(message = "[ERROR] 비밀번호 입력은 필수 입니다.")
        @Size(min = 8, message = "[ERROR] 비밀번호는 최소 8자리 이이어야 합니다.")
        @Schema(description = "password", example = "test1234!!")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,64}$", message = "[ERROR] 비밀번호는 8자 이상, 64자 이하이며 특수문자 한 개를 포함해야 합니다.")
        String password;
    }

    @Getter
    @Builder
    public static class SignupDTO {
        @Size(max = 10, message = "이름은 최대 10자까지 입력 가능합니다.")
        @Schema(description = "name", example = "힘전소")
        @NotBlank(message = "[ERROR] 이름 입력은 필수 입니다.")
        private String name;

        @NotBlank(message = "[ERROR] 이메일 입력은 필수입니다.")
        @Schema(description = "email", example = "test1234@umc.com")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "[ERROR] 이메일 형식에 맞지 않습니다.")
        private String email;

        @NotBlank(message = "[ERROR] 비밀번호 입력은 필수 입니다.")
        @Size(min = 8, message = "[ERROR] 비밀번호는 최소 8자리 이이어야 합니다.")
        @Schema(description = "password", example = "test1234!!")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,64}$", message = "[ERROR] 비밀번호는 8자 이상, 64자 이하이며 특수문자 한 개를 포함해야 합니다.")
        private String password;

        @NotBlank(message = "[ERROR] 비밀번호 재확인 입력은 필수 입니다.")
        @Schema(description = "passwordCheck", example = "test1234!!")
        private String passwordCheck;

        private String provider;

        public Member toEntity(String encodedPw){
            return Member.builder()
                    .email(email)
                    .password(encodedPw)
                    .name(name)
                    .build();
        }

        public Member toEntity() {
            return Member.builder()
                    .email(email)
                    .name(name)
                    .provider(provider)
                    .build();
        }
    }
}
