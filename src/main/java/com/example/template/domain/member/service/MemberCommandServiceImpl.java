package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.ProfileRequestDTO;
import com.example.template.domain.member.dto.ProfileResponseDTO;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.exception.MemberErrorCode;
import com.example.template.domain.member.exception.MemberException;
import com.example.template.domain.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService{

    private final MemberRepository memberRepository;

    public ProfileResponseDTO.ProfileDTO updateProfile(Long memberId, ProfileRequestDTO.UpdateProfileDTO updateProfileDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        //s3에서 프로필 업데이트 필요 + s3에서 이전 프로필 삭제 로직 추후 추가
        member.updateProfile(updateProfileDTO);

        return ProfileResponseDTO.from(member);
    }

//    @Override
//    public Boolean createProfile(Long memberId, ProfileRequestDTO.CreateProfileDTO createProfileDTO) {
//        Member member = ProfileRequestDTO.toEntity(createProfileDTO);
//        //s3에 사진 저장
//        memberRepository.save(member);
//        return true;
//    }

    @Override
    public Long deleteProfile(Long memberId) {

        memberRepository.deleteById(memberId);
        return memberId;
    }
}
