package com.example.template.domain.member.entity;

import com.example.template.domain.member.dto.ProfileRequestDTO;
import com.example.template.global.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(name = "member_email", nullable = false, length = 20)
    private String email;   // 이메일

    @Column(name = "member_name", nullable = false)
    private String name;    // 이름

    @Column(name = "member_nickname", nullable = false)
    private String nickname;    // 닉네임

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "member_password")
    private String password;    // 비밀번호

    @Column(name = "member_role")
    private String role;    // 역할

    @Column(name = "profile_img")
    private String profileImg;  // 프로필 이미지

    @Column(name = "is_reported")
    private boolean isReported; // 신고 상태

    public void updateProfile(ProfileRequestDTO.UpdateProfileDTO updateProfileDTO) {
        this.nickname = updateProfileDTO.getNickname();
        this.profileImg = updateProfileDTO.getProfileImg();
    }
}
