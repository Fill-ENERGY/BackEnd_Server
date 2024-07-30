package com.example.template.domain.board.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class CommentController {

    // TODO : 댓글 CRUD 구현
//    private CommentQueryService commentQueryService;
//    private CommentCommandService commentCommandService;
//
//    @GetMapping
//    public ApiResponse<List<CommentResponseDTO>> getCommentList(@PathVariable Long boardId) {
//        List<CommentResponseDTO> comments = commentQueryService.getCommentList(boardId);
//        return ApiResponse.onSuccess(comments);
//    }
//
//    @PostMapping
//    public ApiResponse<Long> createComment(@PathVariable Long boardId, @RequestBody CommentRequestDTO commentRequestDTO) {
//        Long commentId = commentCommandService.createComment(boardId, commentRequestDTO);
//        return ApiResponse.onSuccess(HttpStatus.CREATED, commentId);
//    }
//
//    @PutMapping("/{commentId}")
//    public ApiResponse<Long> updateComment(@PathVariable Long boardId, @PathVariable Long commentId, @RequestBody CommentRequestDTO commentRequestDTO) {
//        Long updatedCommentId = commentCommandService.updateComment(boardId, commentId, commentRequestDTO);
//        return ApiResponse.onSuccess(updatedCommentId);
//    }
//
//    @DeleteMapping("/{commentId}")
//    public ApiResponse<Void> deleteComment(@PathVariable Long boardId, @PathVariable Long commentId) {
//        commentCommandService.deleteComment(boardId, commentId);
//        return ApiResponse.onSuccess(null);
//    }
}