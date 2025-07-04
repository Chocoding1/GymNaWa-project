package project.gymnawa.domain.gym.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GymDto {

    @JsonProperty("id")
    private String gymId;

    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("distance")
    private double distance;

    @JsonProperty("place_url")
    private String placeUrl;

    @JsonProperty("y")
    private double latitude;

    @JsonProperty("x")
    private double longitude;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("road_address_name")
    private String roadAddress;
}
