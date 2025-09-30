package project.gymnawa.domain.common.error.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@Builder
public class ErrorResponse {

    private HttpStatus status;
    private String errorCode;
    private String errorMessage;
    private Map<String, String> errorFields;

    public static ErrorResponse of(HttpStatus status, String errorCode, String errorMessage) {
        return ErrorResponse.builder()
                .status(status)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    public static ErrorResponse of(HttpStatus status, String errorCode, String errorMessage, Map<String, String> errorFields) {
        return ErrorResponse.builder()
                .status(status)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .errorFields(errorFields)
                .build();
    }
}
