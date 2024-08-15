package com.example.template.domain.member.entity;

import com.example.template.domain.member.dto.ProfileRequestDTO;
import com.example.template.global.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "member_email", nullable = false)
    private String email;   // 이메일

    @Column(name = "member_name", nullable = false)
    private String name;    // 이름

    @Column(name = "member_nickname")
    private String nickname;    // 닉네임

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "member_password")
    private String password;    // 비밀번호

    @Enumerated(EnumType.STRING)
    @Column(name = "member_provider", length = 100) // provider 추가 (kakao)
    private ProviderType provider;

    @Column(name = "member_role")
    private String role;    // 역할

    @Column(name = "profile_img")
    private String profileImg;  // 프로필 이미지

    @Column(name = "is_reported")
    private boolean isReported; // 신고 상태

    @Column(name = "member_birth")
    private LocalDate birth; //생일

    @Column(name = "member_gender")
    private GenderType gender; //성별

    @Column(name = "member_phone", length = 16)
    private String phone;

    @Column(name = "member_type")
    private MemberType memberType;

    public void updateProfile(ProfileRequestDTO.UpdateProfileDTO updateProfileDTO, String imageUrl) {
        this.nickname = updateProfileDTO.getNickname();
        this.memberType = updateProfileDTO.getMemberType();
        this.gender = updateProfileDTO.getGender();
        this.birth = updateProfileDTO.getBirth();
        this.phone = updateProfileDTO.getPhone();
        this.profileImg = imageUrl;
    }

    public void setMemberNickname(String memberNickname) {
        this.nickname = (memberNickname != null && !memberNickname.isEmpty())
                ? memberNickname
                : this.email.split("@")[0];
    }
}
