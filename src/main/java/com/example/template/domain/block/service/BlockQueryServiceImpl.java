package com.example.template.domain.block.service;

import com.example.template.domain.block.dto.response.BlockResponseDTO;
import com.example.template.domain.block.entity.Block;
import com.example.template.domain.block.repository.BlockRepository;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockQueryServiceImpl implements BlockQueryService{

    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;

    @Override
    public List<BlockResponseDTO.BlockListDTO> getBlockList() {
        // TODO 현재 로그인한 멤버 정보 받아오기, 멤버 예외 처리로 변경
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        List<Block> blockList = blockRepository.findByMember(member);
        return blockList.stream()
                .map(BlockResponseDTO.BlockListDTO::fromEntity)
                .collect(Collectors.toList());

    }
}
