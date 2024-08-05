package com.example.template.domain.member.exception;

import com.example.template.global.apiPayload.ApiResponse;
import com.example.template.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    //해당 멤버가 없을 때
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER401", "해당 멤버를 찾을 수 없습니다"),
    //비밀번호 재확인이 올바르지 않을 때
    PASSWORD_NOT_EQUAL(HttpStatus.NOT_FOUND, "MEMBER402", "비밀번호가 일치하지 않습니다."),
    //이메일이 중복될 때
    USER_ALREADY_EXIST(HttpStatus.NOT_FOUND, "MEMBER403", "사용자가 이미 존재합니다."),
    //비밀번호를 잘 못 입력했을 때
    PASSWORD_NOT_MATCH(HttpStatus.NOT_FOUND, "MEMBER404", "비밀번호가 일치하지 않습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}
