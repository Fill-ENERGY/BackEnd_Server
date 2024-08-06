package com.example.template.domain.member.jwt.userdetails;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.exception.MemberErrorCode;
import com.example.template.domain.member.exception.MemberException;
import com.example.template.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        return new PrincipalDetails(member);
    }
}