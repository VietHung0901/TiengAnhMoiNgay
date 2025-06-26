package Project.TiengAnhMoiNgay.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${google.gemini.apiKey}")
    private String apiKey;

    public String getFeedback(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        // Tạo body theo định dạng yêu cầu
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> request = Map.of("contents", List.of(content));

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // Gửi request
        ResponseEntity<Map> response = restTemplate.postForEntity(getUrl(), entity, Map.class);

        // Lấy dữ liệu phản hồi
        try {
            Map<String, Object> contentMap = (Map<String, Object>) ((List<?>) response.getBody().get("candidates")).get(0);
            Map<String, Object> contentObj = (Map<String, Object>) contentMap.get("content");
            Map<String, Object> partObj = (Map<String, Object>) ((List<?>) contentObj.get("parts")).get(0);
            return cleanMarkdown(partObj.get("text").toString());
        } catch (Exception e) {
            return "Không thể phân tích phản hồi.";
        }
    }

    private String getUrl() {
        return "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;
    }

    public String cleanMarkdown(String text) {
        if (text == null) return "";

        // Xoá các ký tự ** dùng để bôi đậm, nhưng giữ lại dấu * thường
        return text.replaceAll("\\*\\*(.*?)\\*\\*", "$1");
    }
}
