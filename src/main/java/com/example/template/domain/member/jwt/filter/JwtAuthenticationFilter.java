package com.example.template.domain.member.jwt.filter;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.jwt.exception.SecurityCustomException;
import com.example.template.domain.member.jwt.userdetails.PrincipalDetails;
import com.example.template.domain.member.jwt.util.HttpResponseUtil;
import com.example.template.domain.member.jwt.util.JwtProvider;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import com.example.template.global.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("[*] Jwt Filter");

        try {
            String accessToken = jwtProvider.resolveAccessToken(request);

            // accessToken 없이 접근할 경우
            if (accessToken != null && jwtProvider.validateRefreshToken(accessToken)) {
                // logout 처리된 accessToken
                if (redisUtil.get(accessToken) != null && redisUtil.get(accessToken).equals("logout")) {
                    log.info("[*] Logout accessToken");
                    // TODO InsufficientAuthenticationException 예외 처리
                    log.info("==================");
                    filterChain.doFilter(request, response);
                    log.info("==================");
                    return;
                }

                log.info("[*] Authorization with Token");
                authenticateAccessToken(accessToken);
            }

            filterChain.doFilter(request, response);
        } catch (SecurityCustomException e) {
            log.warn(">>>>> SecurityCustomException : ", e);
            BaseErrorCode errorCode = e.getErrorCode();
            ApiResponse<String> errorResponse = ApiResponse.onFailure(
                    errorCode.getCode(),
                    errorCode.getMessage(),
                    e.getMessage()
            );
            HttpResponseUtil.setErrorResponse(
                    response,
                    errorCode.getHttpStatus(),
                    errorResponse
            );
        } catch (ExpiredJwtException e) {
            log.warn("[*] case : accessToken Expired");
        }
    }

    private void authenticateAccessToken(String accessToken) {
        // jwtProvider를 통해 이메일을 얻고, memberRepository를 통해 Optional<Member> 객체를 반환
        Optional<Member> optionalMember = memberRepository.findByEmail(jwtProvider.getUserEmail(accessToken));

        // Optional에서 값을 추출합니다. 값이 없으면 예외처리
        Member member = optionalMember.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + jwtProvider.getUserEmail(accessToken)));

        // Member 객체를 사용하여 PrincipalDetails 객체를 생성
        PrincipalDetails userDetails = new PrincipalDetails(member);

        log.info("[*] Authority Registration");

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        // 컨텍스트 홀더에 저장
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}