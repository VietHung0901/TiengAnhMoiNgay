package Project.TiengAnhMoiNgay.controllers.api;

import Project.TiengAnhMoiNgay.services.TranslateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/translate")
@RequiredArgsConstructor
public class ApiTranslateController {

    private final TranslateService translateService;

    @GetMapping
    public ResponseEntity<?> translate(
            @RequestParam String text,
            @RequestParam(defaultValue = "en") String from,
            @RequestParam(defaultValue = "vi") String to
    ) {
        try {
            String result = translateService.translate(text, from, to);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Translate successfully.", "translatedText", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "An error occurred while translate: " + e.getMessage()));
        }
    }
}