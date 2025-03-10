package project.gymnawa.controller.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import project.gymnawa.domain.entity.Member;
import project.gymnawa.domain.entity.NorMember;
import project.gymnawa.domain.api.ApiResponse;
import project.gymnawa.web.SessionConst;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HomeApiController {

    @Value("${api.key}")
    private String apiKey;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Map<String, Object>>> home(
            @SessionAttribute(value = SessionConst.LOGIN_MEMBER, required = false) Member loginedMember) {

        Map<String, Object> response = new HashMap<>();

        response.put("apiKey", apiKey);

        if (loginedMember == null) {
            return ResponseEntity.ok().body(ApiResponse.success(response));
        }

        response.put("member", loginedMember);
        response.put("role", loginedMember instanceof NorMember ? "Normal_Member" : "Trainer");

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }
}
