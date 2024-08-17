package com.example.template.domain.board.controller;

import com.example.template.domain.board.dto.request.BoardRequestDTO;
import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.SortType;
import com.example.template.domain.board.service.commandService.BoardCommandService;
import com.example.template.domain.board.service.queryService.BoardQueryService;
import com.example.template.domain.member.entity.Member;
import com.example.template.global.annotation.AuthenticatedMember;
import com.example.template.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardQueryService boardQueryService;
    private final BoardCommandService boardCommandService;

    // QueryService
    @Operation(summary = "게시글 목록 조회 (전체 및 카테고리별)", description = "커뮤니티 게시글 목록을 전체 또는 카테고리별로 무한 스크롤 방식으로 조회합니다..")
    @GetMapping
    public ApiResponse<BoardResponseDTO.BoardListDTO> getBoardList(
            @RequestParam(required = false) Category category,
            @RequestParam(defaultValue = "0") Long cursor,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "LATEST") SortType sort,
            @AuthenticatedMember Member member) {

        return ApiResponse.onSuccess(boardQueryService.getBoardList(category, cursor, limit, sort, member));
    }

    @Operation(summary = "게시글 상세 조회", description = "지정된 ID의 게시글 상세 정보를 조회합니다.")
    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponseDTO.BoardDTO> getBoardDetail(@PathVariable("boardId") Long boardId,
                                                                       @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(boardQueryService.getBoardDetail(boardId, member));
    }

    @Operation(summary = "내가 쓴 게시글 조회", description = "사용자가 작성한 게시글 목록을 최신순으로 커서 기반 페이지네이션으로 조회합니다.")
    @GetMapping("/my-posts")
    public ApiResponse<BoardResponseDTO.BoardListDTO> getMyPosts(
            @RequestParam(defaultValue = "0") Long cursor,
            @RequestParam(defaultValue = "10") Integer limit,
            @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(boardQueryService.getMyPosts(cursor, limit, member));
    }

    @Operation(summary = "내가 댓글 단 게시글 조회", description = "사용자가 댓글을 작성한 게시글 목록을 최신순으로 커서 기반 페이지네이션으로 조회합니다.")
    @GetMapping("/my-comments")
    public ApiResponse<BoardResponseDTO.BoardListDTO> getMyCommentedPosts(
            @RequestParam(defaultValue = "0") Long cursor,
            @RequestParam(defaultValue = "10") Integer limit,
            @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(boardQueryService.getMyCommentedPosts(cursor, limit, member));
    }

    @Operation(summary = "내가 좋아요 한 게시글 조회", description = "사용자가 좋아요를 누른 게시글 목록을 최신순으로 커서 기반 페이지네이션으로 조회합니다.")
    @GetMapping("/my-likes")
    public ApiResponse<BoardResponseDTO.BoardListDTO> getMyLikedPosts(
            @RequestParam(defaultValue = "0") Long cursor,
            @RequestParam(defaultValue = "10") Integer limit,
            @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(boardQueryService.getMyLikedPosts(cursor, limit, member));
    }

    // CommandService
    @Operation(summary = "커뮤니티 게시글 이미지 업로드", description = "게시글에 첨부할 이미지를 업로드합니다.")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BoardResponseDTO.BoardImgDTO> uploadBoardImages(
            @RequestPart("images") List<MultipartFile> images) {
        return ApiResponse.onSuccess(boardCommandService.uploadBoardImages(images));
    }

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<BoardResponseDTO.BoardDTO>> createBoard(
            @Valid @RequestBody BoardRequestDTO.CreateBoardDTO createBoardDTO,
            @AuthenticatedMember Member member) {
        // TODO: 어차피 게시글 목록 페이지로 리다이렉트 되어서, boardId만 Return 하는 방식 고려
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(HttpStatus.CREATED,
                        boardCommandService.createBoard(createBoardDTO, member)));
    }

    @Operation(summary = "게시글 수정", description = "지정된 ID의 게시글을 수정합니다.")
    @PutMapping("/{boardId}")
    public ApiResponse<BoardResponseDTO.BoardDTO> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequestDTO.UpdateBoardDTO updateBoardDTO,
            @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(boardCommandService.updateBoard(boardId, updateBoardDTO, member));
    }

    @Operation(summary = "게시글 삭제", description = "지정된 ID의 게시글을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ApiResponse<Long> deleteBoard(@PathVariable Long boardId,
                                         @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(boardCommandService.deleteBoard(boardId, member));
    }

    @Operation(summary = "게시글 상태 변경", description = "도와줘요 카테고리의 게시글 상태를 변경합니다.")
    @PatchMapping("/{boardId}/status")
    public ApiResponse<BoardResponseDTO.BoardStatusDTO> updateBoardStatus(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequestDTO.UpdateBoardStatusDTO updateBoardStatusDTO,
            @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(boardCommandService.updateBoardStatus(boardId, updateBoardStatusDTO, member));
    }

    @Operation(summary = "게시글 좋아요 추가", description = "지정된 게시글에 좋아요를 추가합니다.")
    @PostMapping("/{boardId}/likes")
    public ApiResponse<BoardResponseDTO.BoardLikeDTO> addLike(@PathVariable("boardId") Long boardId,
                                                              @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(boardCommandService.addLike(boardId, member));
    }

    @Operation(summary = "게시글 좋아요 삭제", description = "지정된 게시글의 좋아요를 삭제합니다.")
    @DeleteMapping("/{boardId}/likes")
    public ApiResponse<BoardResponseDTO.BoardLikeDTO> removeLike(@PathVariable("boardId") Long boardId,
                                                                 @AuthenticatedMember Member member) {
        return ApiResponse.onSuccess(boardCommandService.removeLike(boardId, member));
    }
}