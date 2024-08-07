package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.ProfileResponseDTO;
import com.example.template.domain.member.entity.Member;

public interface MemberQueryService {

    ProfileResponseDTO.ProfileDTO getProfile(Long memberId);
    Member getMemberByEmail(String email);

}
