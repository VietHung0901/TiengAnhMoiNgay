package Project.TiengAnhMoiNgay.controllers.api;

import Project.TiengAnhMoiNgay.services.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class ApiYoutubeController {
    private final YoutubeService youtubeService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadYoutubeVideo(@RequestParam String youtubeUrl) {
        try {
            String outputDir = "uploads/youtube"; // nhớ tạo folder này hoặc config lại
            youtubeService.downloadSubtitle(youtubeUrl, outputDir);
            youtubeService.downloadAudio(youtubeUrl, outputDir);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Tải audio và phụ đề thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Lỗi xử lý video: " + e.getMessage()
            ));
        }
    }
}
