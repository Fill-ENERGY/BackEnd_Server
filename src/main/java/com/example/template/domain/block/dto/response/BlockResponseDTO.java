package com.example.template.domain.block.dto.response;

import com.example.template.domain.block.entity.Block;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BlockResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlockDTO {
        Long blockId;
        Long memberId;  // 멤버 아이디
        Long targetMemberId;    // 차단 대상 멤버 아이디
    }

    public static BlockDTO fromEntity(Block block) {
        return BlockDTO.builder()
                .blockId(block.getId())
                .memberId(block.getMember().getId())
                .targetMemberId(block.getTargetMember().getId())
                .build();
    }
}
