package project.gymnawa.domain.common.errors.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;
    private Map<String, String> errors;

    public static ErrorResponse of(String errorCode, String errorMessage) {
        return new ErrorResponse(errorCode, errorMessage, null);
    }

    public static ErrorResponse of(String errorCode, String errorMessage, Map<String, String> errors) {
        return new ErrorResponse(errorCode, errorMessage, errors);
    }
}
