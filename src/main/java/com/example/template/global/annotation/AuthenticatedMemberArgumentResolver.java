package com.example.template.global.annotation;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.jwt.userdetails.PrincipalDetails;
import com.example.template.domain.member.service.MemberQueryService;
import com.example.template.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
@Transactional
public class AuthenticatedMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberQueryService memberQueryService;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean parameterAnnotation = parameter.hasParameterAnnotation(AuthenticatedMember.class);
        boolean memberParameterType = parameter.getParameterType().isAssignableFrom(Member.class);
        return parameterAnnotation && memberParameterType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberQueryService.getMemberByEmail(((PrincipalDetails)userDetails).getUsername());
        return member;
    }
}
