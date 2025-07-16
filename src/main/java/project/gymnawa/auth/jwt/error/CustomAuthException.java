package project.gymnawa.auth.jwt.error;

import lombok.Getter;
import project.gymnawa.domain.common.error.dto.ErrorCode;

@Getter
public class CustomAuthException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomAuthException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
