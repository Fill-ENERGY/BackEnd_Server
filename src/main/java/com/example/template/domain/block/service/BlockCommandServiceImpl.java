package com.example.template.domain.block.service;

import com.example.template.domain.block.dto.request.BlockRequestDTO;
import com.example.template.domain.block.dto.response.BlockResponseDTO;
import com.example.template.domain.block.entity.Block;
import com.example.template.domain.block.exception.BlockErrorCode;
import com.example.template.domain.block.exception.BlockException;
import com.example.template.domain.block.repository.BlockRepository;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockCommandServiceImpl implements BlockCommandService {

    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;

    @Override
    public BlockResponseDTO.BlockDTO createBlock(Long targetMemberId) {
        // TODO 현재 로그인한 멤버 정보 받아오기, 멤버 예외 처리로 변경
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        // 자기 자신을 차단하는지 확인
        if (member.equals(targetMember)) {
            throw new BlockException(BlockErrorCode.BLOCK_SELF_BLOCK);
        }

        // 이미 차단된 사용자인지 확인
        boolean exists = blockRepository.existsByMemberAndTargetMember(member, targetMember);
        if (exists) {
            throw new BlockException(BlockErrorCode.BLOCK_ALREADY_EXISTS);
        }

        Block block = BlockRequestDTO.toEntity(member, targetMember);
        block = blockRepository.save(block);

        return BlockResponseDTO.BlockDTO.from(block);
    }

    @Override
    public void deleteBlock(Long blockId) {
        // TODO 현재 로그인한 멤버 정보 받아오기, 멤버 예외 처리로 변경
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new BlockException(BlockErrorCode.BLOCK_NOT_FOUND));

        // 차단 해제 권한이 있는지 확인
        if (!block.getMember().equals(member)) {
            throw new BlockException(BlockErrorCode.ACCESS_DENIED);
        }

        blockRepository.delete(block);
    }
}
