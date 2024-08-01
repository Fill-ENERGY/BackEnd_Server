package com.example.template.domain.board.exception;

import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BoardErrorCode implements BaseErrorCode {

    // BOARD ERROR 응답
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD404", "게시글을 찾을 수 없습니다."),
    UNAUTHORIZED_BOARD_ACCESS(HttpStatus.FORBIDDEN, "BOARD403", "게시글에 대한 권한이 없습니다."),
    HELP_STATUS_UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "BOARD400", "도와줘요 카테고리의 게시글만 상태 변경이 가능합니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "BOARD401", "이미 좋아요를 누른 게시글입니다."),
    LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "BOARD402", "좋아요를 누르지 않은 상태에서는 취소할 수 없습니다."),

    // 사용자 에러
    // TODO : 테스트용. 삭제 예정
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD406", "회원을 찾을 수 없습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
