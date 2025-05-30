package project.gymnawa.errors.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 회원 공통
    INVALID_PASSWORD("INVALID_PASSWORD", HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_NEW_PASSWORD("INVALID_NEW_PASSWORD", HttpStatus.BAD_REQUEST, "새 비밀번호가 서로 일치하지 않습니다."),
    ACCESS_DENIED("ACCESS_DENIED", HttpStatus.BAD_REQUEST, "잘못된 접근입니다."),
    DEACTIVATE_MEMBER("DEACTIVATE_MEMBER", HttpStatus.NOT_FOUND, "탈퇴한 회원입니다."),
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

    // 이메일
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    INVALID_EMAIL_CODE("INVALID_EMAIL_CODE", HttpStatus.BAD_REQUEST, "이메일 인증 코드가 일치하지 않습니다."),

    // 헬스장
    GYM_NOT_FOUND("GYM_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 헬스장입니다."),
    GYMS_NOT_FOUND("GYMS_NOT_FOUND", HttpStatus.NOT_FOUND, "헬스장 조회에 실패했습니다."),

    // 리뷰
    REVIEW_NOT_FOUND("REVIEW_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),

    // 헬스장 - 트레이너 계약
    DUPLICATE_CONTRACT("DUPLICATE_CONTRACT", HttpStatus.CONFLICT, "이미 존재하는 계약입니다."),
    CONTRACT_NOT_FOUND("CONTRACT_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 계약입니다.");


    private final String code;
    private final HttpStatus status;
    private final String errorMessage;
}
