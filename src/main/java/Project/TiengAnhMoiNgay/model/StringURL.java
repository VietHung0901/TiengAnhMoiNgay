package Project.TiengAnhMoiNgay.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class StringURL {
    private final String http = "http://localhost:8080";
}
