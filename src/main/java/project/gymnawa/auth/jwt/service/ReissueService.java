package project.gymnawa.auth.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface ReissueService {

    // 일반 로그인용 메서드
    // 반환 타입 ResponseENtity<ApiResponse<>>로 변경
    ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response);

    // 소셜 로그인용 메서드
    ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response, Long userId);
}
