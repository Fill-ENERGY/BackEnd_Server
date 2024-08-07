package com.example.template.domain.board.controller;

import com.example.template.domain.board.dto.request.BoardRequestDTO;
import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.HelpStatus;
import com.example.template.domain.board.entity.enums.SortType;
import com.example.template.domain.board.scheduler.UnmappedImageCleanupScheduler;
import com.example.template.domain.board.service.commandService.BoardCommandService;
import com.example.template.domain.board.service.queryService.BoardQueryService;
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
    private final UnmappedImageCleanupScheduler unmappedImageCleanupScheduler;

    // TODO : 테스트용 수동 트리거 컨트롤러 - 삭제 예정
    @Operation(summary = "테스트용 수동 트리거", description = "board와 매핑이 안된 baordimg를 삭제합니다.")
    @PostMapping("/cleanupUnmappedImages")
    public ApiResponse<String> cleanupUnmappedImages() {
        unmappedImageCleanupScheduler.manualCleanup();
        return ApiResponse.onSuccess("삭제 완료");
    }

    @Operation(summary = "이미지 업로드", description = "게시글에 첨부할 이미지를 업로드합니다.")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BoardResponseDTO.BoardImgDTO> uploadImages(
            @RequestPart("images") List<MultipartFile> images) {
        return ApiResponse.onSuccess(boardCommandService.uploadImages(images));
    }

    @Operation(summary = "게시글 목록 조회 (전체 및 카테고리별)", description = "커뮤니티 게시글 목록을 전체 또는 카테고리별로 무한 스크롤 방식으로 조회합니다..")
    @GetMapping
    public ApiResponse<BoardResponseDTO.BoardListDTO> getBoardList(
            @RequestParam(required = false) Category category,
            @RequestParam(defaultValue = "0") Long cursor,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "LATEST") SortType sort) {

        return ApiResponse.onSuccess(boardQueryService.getBoardList(category, cursor, limit, sort));
    }

    @Operation(summary = "게시글 상세 조회", description = "지정된 ID의 게시글 상세 정보를 조회합니다.")
    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponseDTO.BoardDetailDTO> getBoardDetail(@PathVariable("boardId") Long boardId) {
        return ApiResponse.onSuccess(boardQueryService.getBoardDetail(boardId));
    }

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<BoardResponseDTO.BoardDTO>> createBoard(
            @Valid @RequestBody BoardRequestDTO.CreateBoardDTO createBoardDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(HttpStatus.CREATED, boardCommandService.createBoard(createBoardDTO)));
    }

    @Operation(summary = "게시글 수정", description = "지정된 ID의 게시글을 수정합니다.")
    @PutMapping("/{boardId}")
    public ApiResponse<BoardResponseDTO.BoardDTO> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequestDTO.UpdateBoardDTO updateBoardDTO) {
        return ApiResponse.onSuccess(boardCommandService.updateBoard(boardId, updateBoardDTO));
    }

    @Operation(summary = "게시글 삭제", description = "지정된 ID의 게시글을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ApiResponse<Long> deleteBoard(@PathVariable Long boardId) {
        return ApiResponse.onSuccess(boardCommandService.deleteBoard(boardId));
    }

    @Operation(summary = "게시글 상태 변경", description = "도와줘요 카테고리의 게시글 상태를 변경합니다.")
    @PatchMapping("/{boardId}/status")
    public ApiResponse<BoardResponseDTO.BoardStatusDTO> updateBoardStatus(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequestDTO.UpdateBoardStatusDTO updateBoardStatusDTO) {
        return ApiResponse.onSuccess(boardCommandService.updateBoardStatus(boardId, updateBoardStatusDTO));
    }

    @Operation(summary = "게시글 좋아요 추가", description = "지정된 게시글에 좋아요를 추가합니다.")
    @PostMapping("/{boardId}/likes")
    public ApiResponse<BoardResponseDTO.BoardLikeDTO> addLike(@PathVariable("boardId") Long boardId) {
        return ApiResponse.onSuccess(boardCommandService.addLike(boardId));
    }

    @Operation(summary = "게시글 좋아요 삭제", description = "지정된 게시글의 좋아요를 삭제합니다.")
    @DeleteMapping("/{boardId}/likes")
    public ApiResponse<BoardResponseDTO.BoardLikeDTO> removeLike(@PathVariable("boardId") Long boardId) {
        return ApiResponse.onSuccess(boardCommandService.removeLike(boardId));
    }
}