package com.example.template.domain.block.controller;

import com.example.template.domain.block.dto.response.BlockResponseDTO;
import com.example.template.domain.block.service.BlockCommandService;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BlockController {

    private final BlockCommandService blockCommandService;

    @PostMapping("/blocks/members/{targetMemberId}")
    @Operation(summary = "차단하기 API", description = "차단하려는 멤버의 아이디를 전달해주세요.")
    public ApiResponse<BlockResponseDTO.BlockDTO> createBlock(@PathVariable(name = "targetMemberId") Long targetMemberId) {
        BlockResponseDTO.BlockDTO blockDTO = blockCommandService.createBlock(targetMemberId);
        return ApiResponse.onSuccess(blockDTO);
    }
}
