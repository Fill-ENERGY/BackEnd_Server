package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.*;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.exception.MemberErrorCode;
import com.example.template.domain.member.exception.MemberException;
import com.example.template.domain.member.jwt.userdetails.PrincipalDetails;
import com.example.template.domain.member.jwt.util.JwtProvider;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.global.util.RedisUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoServiceImpl implements KakaoService{
    private final Environment env;
    private final RestTemplate restTemplate;
    private final Gson gson;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Value("${spring.url.base}")
    private String baseUrl;

    @Value("${social.kakao.client-id}")
    private String kakaoClientId;

    @Value("${social.kakao.redirect}")
    private String kakaoRedirectUri;

    // 토큰 정보 가져오기
    @Override
    public RetKakaoOAuth getKakaoTokenInfo(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", baseUrl + kakaoRedirectUri);
        params.add("code", code);

        String requestUri = env.getProperty("social.kakao.url.token");
        if (requestUri == null) throw new MemberException(MemberErrorCode._INTERNAL_SERVER_ERROR);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(requestUri, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK)
            return gson.fromJson(response.getBody(), RetKakaoOAuth.class);
        throw new MemberException(MemberErrorCode._INTERNAL_SERVER_ERROR);
    }

    //사용자 정보 조회 요청 보내기
    @Override
    public KakaoProfile getKakaoProfile(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + kakaoAccessToken);

        String requestUrl = env.getProperty("social.kakao.url.profile");
        if (requestUrl == null) throw new MemberException(MemberErrorCode._INTERNAL_SERVER_ERROR);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK)
                return gson.fromJson(response.getBody(), KakaoProfile.class);
        } catch (Exception e) {
            log.error(e.toString());
            throw new MemberException(MemberErrorCode._INTERNAL_SERVER_ERROR);
        }
        throw new MemberException(MemberErrorCode._INTERNAL_SERVER_ERROR);
    }

    @Override
    public void kakaoUnlink(String accessToken) {
        String unlinkUrl = env.getProperty("social.kakao.url.unlink");
        if (unlinkUrl == null) throw new MemberException(MemberErrorCode._INTERNAL_SERVER_ERROR);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(unlinkUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) return;
        throw new MemberException(MemberErrorCode._INTERNAL_SERVER_ERROR);
    }

    @Override
    public MemberResponseDTO.SignupResultDTO signupByKakao(SocialRequestDTO.SignupDTO requestDTO) {
        KakaoProfile  kakaoProfile = getKakaoProfile(requestDTO.getAccessToken());
        if (kakaoProfile == null) throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        if (kakaoProfile.getKakao_account().getEmail() == null) {
            kakaoUnlink(requestDTO.getAccessToken());
            throw new MemberException(MemberErrorCode.EMAIL_NOT_EXIST);
        }

        MemberRequestDTO.SignupDTO signupRequestDto = MemberRequestDTO.SignupDTO.builder()
                .email(kakaoProfile.getKakao_account().getEmail())
                .name(kakaoProfile.getProperties().getNickname())
                .provider("kakao")
                .build();

        return memberService.socialSignup(signupRequestDto);
    }

    @Override
    public MemberResponseDTO.LoginResultDTO loginByKakao(SocialRequestDTO.LoginDTO requestDTO) {
        KakaoProfile kakaoProfile = getKakaoProfile(requestDTO.getAccessToken());
        if (kakaoProfile == null) throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);

        String kakaoEmail = kakaoProfile.getKakao_account().getEmail();
        if (kakaoEmail == null) throw new MemberException(MemberErrorCode.EMAIL_NOT_EXIST);

        Member member = memberRepository.findByEmailAndProvider(kakaoEmail, "kakao")
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        PrincipalDetails userDetails = new PrincipalDetails(member);

        String accessToken = jwtProvider.createJwtAccessToken(userDetails);
        String refreshToken = jwtProvider.createJwtRefreshToken(userDetails);

        return MemberResponseDTO.LoginResultDTO.builder()
                .userId(member.getId())
                .createdAt(LocalDateTime.now())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
