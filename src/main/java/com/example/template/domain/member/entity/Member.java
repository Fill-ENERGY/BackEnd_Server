package com.example.template.domain.member.entity;

import com.example.template.global.common.BaseEntity;
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

    @Column(nullable = false)
    private String email;   // 이메일

    @Column(nullable = false)
    private String name;    // 이름

    @Column(name = "profile_img")
    private String profileImg;  // 프로필 이미지

    @Column(name = "is_reported")
    private boolean isReported; // 신고 상태
}
