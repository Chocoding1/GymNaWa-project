package project.gymnawa.domain.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class KakaoApiResponse<T> {

    @JsonProperty("meta")
    private MetaData meta;

    @JsonProperty("documents")
    private List<T> documents;
}
