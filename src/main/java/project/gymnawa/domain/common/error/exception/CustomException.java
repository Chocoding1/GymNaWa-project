package project.gymnawa.domain.common.error.exception;

import lombok.Getter;
import project.gymnawa.domain.common.error.dto.ErrorCode;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
