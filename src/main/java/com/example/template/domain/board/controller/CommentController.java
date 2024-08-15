package com.example.template.domain.board.controller;


import com.example.template.domain.board.dto.request.CommentRequestDTO;
import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.dto.response.CommentResponseDTO;
import com.example.template.domain.board.service.commandService.CommentCommandService;
import com.example.template.domain.board.service.queryService.CommentQueryService;
import com.example.template.domain.member.entity.Member;
import com.example.template.global.annotation.AuthenticatedMember;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class CommentController {

    private final CommentQueryService commentQueryService;
    private final CommentCommandService commentCommandService;

    // QueryService
    @Operation(summary = "댓글 목록 조회", description = "커뮤니티 게시글의 목록을 조회합니다..")
    @GetMapping("/{boardId}/comments")
    public ApiResponse<CommentResponseDTO.CommentsListDTO> getComments(
            @PathVariable Long boardId,
            @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(commentQueryService.getCommentsList(boardId, member));
    }

    // CommandService
    @Operation(summary = "이미지 업로드", description = "댓글에 첨부할 이미지를 업로드합니다.")
    @PostMapping(value = "/comments/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CommentResponseDTO.CommentImgDTO> uploadCommentImages(
            @RequestPart("images") List<MultipartFile> images) {
        return ApiResponse.onSuccess(commentCommandService.uploadCommentImages(images));
    }

    @PostMapping("/{boardId}/comments")
    @Operation(summary = "댓글 작성", description = "게시글에 새 댓글 또는 대댓글을 작성합니다.")
    public ApiResponse<CommentResponseDTO.CommentDTO> createComment(
            @PathVariable("boardId") Long boardId,
            @RequestBody CommentRequestDTO.CreateDTO createDTO,
            @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(commentCommandService.createComment(boardId, createDTO, member));
    }

    @PutMapping("/{boardId}/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "게시글의 특정 댓글을 수정합니다.")
    public ApiResponse<CommentResponseDTO.CommentDTO> updateComment(
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentRequestDTO.UpdateDTO updateDTO,
            @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(commentCommandService.updateComment(boardId, commentId, updateDTO, member));
    }

    @DeleteMapping("/{boardId}/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ApiResponse<Long> deleteComment(
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId,
            @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(commentCommandService.deleteComment(boardId, commentId, member));
    }
}