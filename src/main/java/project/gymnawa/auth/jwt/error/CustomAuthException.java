package project.gymnawa.auth.jwt.error;

import lombok.Getter;
import project.gymnawa.domain.common.error.dto.ErrorCode;

@Getter
public class CustomJwtException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomJwtException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
