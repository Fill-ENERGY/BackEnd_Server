package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.ProfileRequestDTO;
import com.example.template.domain.member.dto.ProfileResponseDTO;
import com.example.template.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

public interface MemberCommandService {

    ProfileResponseDTO.ProfileDTO updateProfile(Member member, MultipartFile file, ProfileRequestDTO.UpdateProfileDTO updateProfileDTO);

}
