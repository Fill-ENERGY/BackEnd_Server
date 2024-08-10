package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.ProfileRequestDTO;
import com.example.template.domain.member.dto.ProfileResponseDTO;

public interface MemberCommandService {

    ProfileResponseDTO.ProfileDTO updateProfile(Long memberId, ProfileRequestDTO.UpdateProfileDTO updateProfileDTO);

}
