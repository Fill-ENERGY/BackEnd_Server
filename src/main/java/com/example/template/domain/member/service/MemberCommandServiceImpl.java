package com.example.template.domain.member.service;

import com.example.template.domain.member.dto.ProfileRequestDTO;
import com.example.template.domain.member.dto.ProfileResponseDTO;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.global.config.aws.S3Manager;
import com.example.template.global.util.s3.entity.Uuid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@AllArgsConstructor
@Service
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService{

    private final S3Manager s3Manager;
    @Override
    public ProfileResponseDTO.MyProfileDTO updateProfile(Member member, MultipartFile file, ProfileRequestDTO.UpdateProfileDTO updateProfileDTO) {

        s3Manager.deleteFile(member.getProfileImg());

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = Uuid.builder().uuid(uuid).build();
        String key = s3Manager.generateProfileKeyName(savedUuid);
        String imageUrl = s3Manager.uploadFile(key, file);

        member.updateProfile(updateProfileDTO, imageUrl);

        return ProfileResponseDTO.MyProfileDTO.from(member);
    }
}
