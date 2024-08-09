package com.example.template.domain.message.exception;

import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MessageErrorCode implements BaseErrorCode {

    // MessageThread ERROR 응답
    THREAD_NOT_FOUND(HttpStatus.NOT_FOUND, "THREAD404", "채팅방을 찾을 수 없습니다."),

    // Message ERROR 응답
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE404", "쪽지를 찾을 수 없습니다."),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "MESSAGE403_1", "보낸 사람 또는 받는 사람이 아닙니다. 권한이 없습니다."),
    SELF_MESSAGE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "MESSAGE403_2", "자기 자신과의 쪽지는 허용되지 않습니다."),
    BLOCKED_MEMBER_NOT_ALLOWED(HttpStatus.FORBIDDEN, "MESSAGE403_3", "차단한 멤버에게는 쪽지를 보낼 수 없습니다."),
    INVALID_IMAGE_URLS(HttpStatus.BAD_REQUEST, "MESSAGE400", "일부 이미지 URL이 유효하지 않거나 찾을 수 없습니다."),

    // MessageParticipant ERROR 응답
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "PARTICIPANT404_1", "채팅방에서 해당 참여자를 찾을 수 없습니다."),
    OTHER_PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "PARTICIPANT404_2", "쪽지 상대를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
