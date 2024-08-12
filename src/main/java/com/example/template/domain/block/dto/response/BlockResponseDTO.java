package com.example.template.domain.block.dto.response;

import com.example.template.domain.block.entity.Block;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class BlockResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlockDTO {
        Long blockId;
        Long memberId;  // 멤버 아이디
        Long targetMemberId;    // 차단 대상 멤버 아이디
        String name;    // 차단 대상 멤버 이름
        String nickname;   // 차단 대상 멤버 닉네임
        String profileImg;  // 차단 대상 멤버 프로필 이미지

        public static BlockDTO from(Block block) {
            return BlockDTO.builder()
                    .blockId(block.getId())
                    .memberId(block.getMember().getId())
                    .targetMemberId(block.getTargetMember().getId())
                    .name(block.getTargetMember().getName())
                    .nickname(block.getTargetMember().getNickname())
                    .profileImg(block.getTargetMember().getProfileImg())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlockListDTO {
        private List<BlockDTO> blocks;
        private Long nextCursor;
        private boolean hasNext;

        public static BlockListDTO of(List<Block> blocks, Long nextCursor, boolean hasNext) {
            List<BlockDTO> blockDTOS = blocks.stream()
                    .map(BlockDTO::from)
                    .toList();

            return BlockListDTO.builder()
                    .blocks(blockDTOS)
                    .nextCursor(nextCursor)
                    .hasNext(hasNext)
                    .build();
        }
    }
}
