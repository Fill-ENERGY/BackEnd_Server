package com.example.template.domain.block.controller;

import com.example.template.domain.block.dto.response.BlockResponseDTO;
import com.example.template.domain.block.service.BlockCommandService;
import com.example.template.domain.block.service.BlockQueryService;
import com.example.template.domain.member.entity.Member;
import com.example.template.global.annotation.AuthenticatedMember;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BlockController {

    private final BlockCommandService blockCommandService;
    private final BlockQueryService blockQueryService;

    @PostMapping("/blocks/members/{targetMemberId}")
    @Operation(summary = "차단하기", description = "차단할 멤버의 아이디를 전달해주세요.")
    public ApiResponse<BlockResponseDTO.BlockDTO> createBlock(@PathVariable("targetMemberId") Long targetMemberId, @AuthenticatedMember Member member) {
        BlockResponseDTO.BlockDTO blockDTO = blockCommandService.createBlock(targetMemberId, member);
        return ApiResponse.onSuccess(blockDTO);
    }

    @GetMapping("/blocks/members")
    @Operation(summary = "차단 목록 조회", description = "멤버가 차단한 목록을 조회합니다. 차단 목록이 없으면 빈 배열을 반환합니다.")
    public ApiResponse<BlockResponseDTO.BlockListDTO> getBlockList(@RequestParam(defaultValue = "0") Long cursor,
                                                                         @RequestParam(defaultValue = "10") Integer limit,
                                                                         @AuthenticatedMember Member member) {
        BlockResponseDTO.BlockListDTO blockListDTO = blockQueryService.getBlockList(cursor, limit, member);
        return ApiResponse.onSuccess(blockListDTO);
    }

    @DeleteMapping("/blocks/{blockId}")
    @Operation(summary = "차단 해제")
    public ApiResponse<Void> deleteBlock(@PathVariable("blockId") Long blockId, @AuthenticatedMember Member member) {
        blockCommandService.deleteBlock(blockId, member);
        return ApiResponse.onSuccess(null);
    }
}
