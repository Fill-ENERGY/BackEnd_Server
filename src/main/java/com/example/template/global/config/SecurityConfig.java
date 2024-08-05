package com.example.template.global.config;

import com.example.template.domain.member.jwt.filter.JwtAuthenticationFilter;
import com.example.template.domain.member.jwt.filter.JwtExceptionFilter;
import com.example.template.domain.member.jwt.handler.JwtAccessDeniedHandler;
import com.example.template.domain.member.jwt.handler.JwtAuthenticationEntryPoint;
import com.example.template.domain.member.jwt.util.JwtProvider;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final String[] swaggerUrls = {"/swagger-ui/**", "/v3/**"};
    private final String[] authUrls = {"/", "/api/v1/members/signup/**", "/members/social/**", "/api/v1/members/login/**",
            "/api/v1/members/reissue/**"}; // TODO 추후 API 개발
    private final String[] allowedUrls = Stream.concat(Arrays.stream(swaggerUrls), Arrays.stream(authUrls))
            .toArray(String[]::new);

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CORS 정책 설정
        http
                .cors(cors -> cors
                        .configurationSource(CorsConfig.apiConfigurationSource()));
        // csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);
        // form 로그인 방식 disable
        http
                .formLogin(AbstractHttpConfigurer::disable);
        // http basic 인증 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, redisUtil, memberRepository), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class);
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(allowedUrls).permitAll() // 허용된 경로만 접근 허용
                        .anyRequest().authenticated() // 다른 모든 요청은 인증 필요
                );

        // 예외 처리
        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}