package project.gymnawa.auth.jwt.error;

import lombok.Getter;
import project.gymnawa.domain.common.error.dto.ErrorCode;

@Getter
public class JwtException extends RuntimeException {

    private final ErrorCode errorCode;

    public JwtException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
