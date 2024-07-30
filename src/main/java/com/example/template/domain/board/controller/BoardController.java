package com.example.template.domain.board.controller;

import com.example.template.domain.board.dto.request.BoardRequestDTO;
import com.example.template.domain.board.dto.response.BoardResponseDTO;
import com.example.template.domain.board.entity.enums.Category;
import com.example.template.domain.board.entity.enums.HelpStatus;
import com.example.template.domain.board.service.commandService.BoardCommandService;
import com.example.template.domain.board.service.queryService.BoardQueryService;
import com.example.template.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardQueryService boardQueryService;
    private final BoardCommandService boardCommandService;

    // TODO: 커서 적용해서 목록 조회 예정
    // 게시글 목록 조회 (전체 및 카테고리별)
//    @GetMapping("/{category}/{cursor}/{limit}")
//    public ApiResponse<BoardResponseDTO.BoardListDTO> getBoardList(
//            @PathVariable(required = false) Category category,
//            @PathVariable Long cursor,
//            @PathVariable Integer limit,
//            @RequestParam(defaultValue = "latest") String sort) {
//        return ApiResponse.onSuccess(boardQueryService.getBoardList(category, cursor, limit, sort));
//    }

    // 게시글 상세 조회 (단일 게시글)
    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponseDTO.BoardDetailDTO> getBoardDetail(@PathVariable Long boardId) {
        return ApiResponse.onSuccess(boardQueryService.getBoardDetail(boardId));
    }

    // TODO : 내 게시글 조회 (보류 : 아직 디자인 안나옴)

    // 게시글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<BoardResponseDTO.BoardDTO>> createBoard(
            @Valid @RequestBody BoardRequestDTO.CreateBoardDTO createBoardDTO) {
        // 201 Created 사용
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(HttpStatus.CREATED, boardCommandService.createBoard(createBoardDTO)));
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    public ApiResponse<BoardResponseDTO.BoardDTO> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequestDTO.UpdateBoardDTO updateBoardDTO) {
        return ApiResponse.onSuccess(boardCommandService.updateBoard(boardId, updateBoardDTO));
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ApiResponse<Long> deleteBoard(@PathVariable Long boardId) {
        return ApiResponse.onSuccess(boardCommandService.deleteBoard(boardId));
    }

    // 게시글 상태 변경 (도와줘요 카테고리)
    @PatchMapping("/{boardId}/status")
    public ApiResponse<BoardResponseDTO.BoardStatusDTO> updateBoardStatus(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequestDTO.UpdateBoardStatusDTO updateBoardStatusDTO) {
        return ApiResponse.onSuccess(boardCommandService.updateBoardStatus(boardId, updateBoardStatusDTO));
    }

    // 게시글 좋아요 추가
    @PostMapping("/{boardId}/likes")
    public ApiResponse<BoardResponseDTO.BoardLikeDTO> addLike(@PathVariable Long boardId) {
        return ApiResponse.onSuccess(boardCommandService.addLike(boardId));
    }

    // 게시글 좋아요 삭제
    @DeleteMapping("/{boardId}/likes")
    public ApiResponse<BoardResponseDTO.BoardLikeDTO> removeLike(@PathVariable Long boardId) {
        return ApiResponse.onSuccess(boardCommandService.removeLike(boardId));
    }
}