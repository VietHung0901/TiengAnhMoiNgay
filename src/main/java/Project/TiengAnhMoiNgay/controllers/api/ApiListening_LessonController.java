package Project.TiengAnhMoiNgay.controllers.api;

import Project.TiengAnhMoiNgay.entities.Listening_lessons;
import Project.TiengAnhMoiNgay.entities.Subtitle_lines;
import Project.TiengAnhMoiNgay.model.StringURL;
import Project.TiengAnhMoiNgay.request.Listening_LessonCreate;
import Project.TiengAnhMoiNgay.response.Listening_LessonGet;
import Project.TiengAnhMoiNgay.services.ListeningLessonService;
import Project.TiengAnhMoiNgay.services.YoutubeSubtitleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/listening_lesson")
@RequiredArgsConstructor
public class ApiListening_LessonController {

    StringURL dir = new StringURL();
    private final ListeningLessonService lessonService;
    private final YoutubeSubtitleService ytService;

    @GetMapping("/list/{pageNumber}")
    public ResponseEntity<?> listLesson(@PathVariable("pageNumber") int pageNumber) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, 10);
            Page<Listening_lessons> pageLessons = lessonService.Pageable_ListeningLessons(pageable);

            List<Listening_LessonGet> listLesson = pageLessons.stream().map(lesson -> Listening_LessonGet.builder().Id(lesson.getId()).title(lesson.getTitle()).youtubeUrl(lesson.getYoutubeUrl()).status(lesson.getStatus()).build()).toList();

            return ResponseEntity.ok(Map.of("status", "success", "message", "List of listening lessons retrieved successfully.", "data", listLesson, "currentPage", pageLessons.getNumber(), "totalItems", pageLessons.getTotalElements(), "totalPages", pageLessons.getTotalPages()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "An error occurred while fetching the lessons: " + e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLesson(@RequestBody @Valid Listening_LessonCreate listening_lesson) {

        String youtubeUrl = listening_lesson.getYoutubeUrl(), title = listening_lesson.getTitle();

        if (lessonService.isYoutubeUrlExists(youtubeUrl)) {
            return ResponseEntity.ok(Map.of("status", "error", "message", "This lesson already exists."));
        }

        // Kiểm tra url cung cấp có tồn tại video youtube không
        if (!ytService.isYoutubeVideoExists(youtubeUrl)) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Video not found or not accessible."));
        }

        youtubeUrl = youtubeUrl.split("&")[0];
        String videoId = ytService.extractVideoId(youtubeUrl);
        Path filePath = Paths.get(dir.getDirSubtitles_sub(), videoId + ".srt");

        try {
            // Gọi bất đồng bộ
            lessonService.createLesson(title, youtubeUrl, videoId);
            // Trả về phản hồi ngay cho client
            return ResponseEntity.ok(Map.of("status", "success", "message", "Lesson is being generated in background. Please check status later."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> detailsLesson(@PathVariable("id") Long listening_lessonId) {
        Optional<Listening_lessons> listening_lesson = lessonService.getListeningLessonById(listening_lessonId);
        try {
            if (listening_lesson.isEmpty()) {
                return ResponseEntity.ok(Map.of("status", "error", "message", "This lesson not exists."));
            }
            // Trả về phản hồi ngay cho client
            return ResponseEntity.ok(Map.of("status", "success", "message", "listening lessons retrieved successfully.", "data", listening_lesson));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping(value = "/check-voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> checkVoice(
            @RequestParam("voice") MultipartFile voiceFile) {
        try {
            // Kiểm tra xem tệp voice có được gửi lên không
            if (voiceFile == null || voiceFile.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("status", "error", "message", "Tệp ghi âm không được gửi."));
            }

            // Lưu tệp ghi âm tạm thời vào thư mục tạm
            String tempDir = dir.getDirSubtitles_audio();
            File tempDirFile = new File(tempDir);
            if (!tempDirFile.exists()) {
                tempDirFile.mkdirs(); // Tạo thư mục nếu chưa tồn tại
            }

            String nameTempAudioFile = "temp_" + System.currentTimeMillis();
            File tempAudioFile = new File(tempDir, nameTempAudioFile + ".wav");
            voiceFile.transferTo(tempAudioFile); // Lưu tệp vào đĩa

            // Đường dẫn tới tệp âm thanh tạm
            String audioPath = tempAudioFile.getAbsolutePath();

            // Dùng Whisper để tạo file srt từ audio
            File srtFile = ytService.generateSRTWithWhisper(nameTempAudioFile, dir.getDirSubtitles_sub(), audioPath);

            // Parse phụ đề và gán vào lesson
            String lines = ytService.parseSrtFromVoice(srtFile);

            // Xóa tệp tạm sau khi xử lý
            tempAudioFile.delete();
            srtFile.delete();

            // Trả về phản hồi ngay cho client
            return ResponseEntity.ok(Map.of("status", "success", "message", "Generate text from voice successlly!", "data", lines));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Lỗi khi lưu tệp âm thanh: " + e.getMessage()));
        } catch (InterruptedException e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Lỗi khi xử lý Whisper: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Lỗi không xác định: " + e.getMessage()));
        }
    }
}
