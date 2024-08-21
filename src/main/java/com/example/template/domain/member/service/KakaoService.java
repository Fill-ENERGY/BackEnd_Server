package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.KakaoProfile;
import com.example.template.domain.member.dto.MemberResponseDTO;
import com.example.template.domain.member.dto.RetKakaoOAuth;
import com.example.template.domain.member.dto.SocialRequestDTO;

public interface KakaoService {
    RetKakaoOAuth getKakaoTokenInfo(String code);

    void kakaoUnlink(String accessToken);

    MemberResponseDTO.LoginResultDTO loginOrSignupByKakao(SocialRequestDTO.LoginDTO requestDTO);

    MemberResponseDTO.LoginResultDTO androidKakao(String accessToken);
}
