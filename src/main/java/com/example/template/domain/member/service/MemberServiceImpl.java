package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.MemberRequestDTO;
import com.example.template.domain.member.dto.MemberResponseDTO;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.exception.MemberErrorCode;
import com.example.template.domain.member.exception.MemberException;
import com.example.template.domain.member.jwt.dto.JwtDTO;
import com.example.template.domain.member.jwt.exception.SecurityCustomException;
import com.example.template.domain.member.jwt.exception.TokenErrorCode;
import com.example.template.domain.member.jwt.userdetails.PrincipalDetails;
import com.example.template.domain.member.jwt.util.JwtProvider;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    @Override
    public MemberResponseDTO.SignupResultDTO signup(MemberRequestDTO.SignupDTO signupDTO) {

        // pw, pw 확인 일치 확인
        if (!signupDTO.getPassword().equals(signupDTO.getPasswordCheck()))
            throw new MemberException(MemberErrorCode.PASSWORD_NOT_EQUAL);

        // 이메일 중복 확인
        if (memberRepository.existsByEmail(signupDTO.getEmail())) {
            throw new MemberException(MemberErrorCode.USER_ALREADY_EXIST);
        }

        String encodedPw = passwordEncoder.encode(signupDTO.getPassword());
        Member member = signupDTO.toEntity(encodedPw);

        return MemberResponseDTO.SignupResultDTO.from(memberRepository.save(member));
    }

    @Override
    public MemberResponseDTO.LoginResultDTO login(MemberRequestDTO.CustomLoginDTO loginDTO) {
        // 회원 정보 존재 하는지 확인
        Member member = memberRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 회원 pw 일치 여부
        if (!passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.PASSWORD_NOT_MATCH);
        }

        PrincipalDetails memberDetails = new PrincipalDetails(member);

        // 로그인 성공 시 토큰 생성
        String accessToken = jwtProvider.createJwtAccessToken(memberDetails);
        String refreshToken = jwtProvider.createJwtRefreshToken(memberDetails);

        return MemberResponseDTO.LoginResultDTO.from(member, accessToken, refreshToken);
    }

    @Override
    public MemberResponseDTO.SignupResultDTO socialSignup(MemberRequestDTO.SignupDTO signupDTO) {
        if (memberRepository
                .findByEmailAndProvider(signupDTO.getEmail(), signupDTO.getProvider())
                .isPresent()
        ) throw new MemberException(MemberErrorCode.USER_ALREADY_EXIST);
        Member member = memberRepository.save(signupDTO.toEntity());

        member.setMemberNickname(member.getNickname());

        return MemberResponseDTO.SignupResultDTO.from(member);
    }

    @Override
    public void logout(HttpServletRequest request) {
        try {
            String accessToken = jwtProvider.resolveAccessToken(request);

            // 블랙리스트에 저장
            redisUtil.save(
                    accessToken,
                    "logout",
                    jwtProvider.getExpTime(accessToken),
                    TimeUnit.MILLISECONDS
            );

            redisUtil.delete(
                    jwtProvider.getUserEmail(accessToken)
            );
        } catch (ExpiredJwtException e) {
            throw new SecurityCustomException(TokenErrorCode.TOKEN_EXPIRED);
        }
    }

    @Override
    public JwtDTO reissueToken(String refreshToken) {
        try {
            jwtProvider.validateRefreshToken(refreshToken);
            return jwtProvider.reissueToken(refreshToken);
        } catch (ExpiredJwtException eje) {
            throw new SecurityCustomException(TokenErrorCode.TOKEN_EXPIRED, eje);
        } catch (IllegalArgumentException iae) {
            throw new SecurityCustomException(TokenErrorCode.INVALID_TOKEN, iae);
        }
    }
}
