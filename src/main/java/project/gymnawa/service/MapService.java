package project.gymnawa.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapService {

    @Value("${api.key}")
    private final String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}
