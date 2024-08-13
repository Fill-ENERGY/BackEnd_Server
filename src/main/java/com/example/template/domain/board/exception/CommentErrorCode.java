package com.example.template.domain.board.exception;

import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements BaseErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT404", "댓글을 찾을 수 없습니다."),
    NOT_COMMENT_OWNER(HttpStatus.FORBIDDEN, "COMMENT403", "댓글의 소유자가 아닙니다."),
    COMMENT_BOARD_MISMATCH(HttpStatus.BAD_REQUEST, "COMMENT400", "댓글이 해당 게시글에 속하지 않습니다."),
    INVALID_IMAGE_URLS(HttpStatus.BAD_REQUEST, "COMMENT400", "일부 이미지 URL이 유효하지 않거나 찾을 수 없습니다."),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMMENT400", "부모 댓글을 찾을 수 없거나 해당 게시글에 속하지 않습니다."),
    NESTED_REPLY_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "COMMENT400", "대댓글에 대한 답글은 작성할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}