package Project.TiengAnhMoiNgay.controllers.api;

import Project.TiengAnhMoiNgay.entities.Listening_lesson;
import Project.TiengAnhMoiNgay.entities.Writing_lesson;
import Project.TiengAnhMoiNgay.request.Writing_LessonCreate;
import Project.TiengAnhMoiNgay.response.Listening_LessonGet;
import Project.TiengAnhMoiNgay.response.Writing_LessonGet;
import Project.TiengAnhMoiNgay.services.WritingLessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/writing_lesson")
@RequiredArgsConstructor
public class ApiWriting_LessonController {
    private final WritingLessonService writingLessonService;

    @GetMapping("/list/{pageNumber}")
    public ResponseEntity<?> listListeningLesson(@PathVariable("pageNumber") int pageNumber, @RequestParam(value = "status", required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, 10);
            Page<Writing_lesson> pageLessons = writingLessonService.Pageable_WritingLessons(pageable, status);

            List<Writing_LessonGet> writeLesson = pageLessons.stream().map(lesson -> Writing_LessonGet.builder().Id(lesson.getId()).title(lesson.getTitle()).level(lesson.getLevel().getName()).status(lesson.getStatus()).build()).toList();

            return ResponseEntity.ok(Map.of("status", "success", "message", "List of listening lessons retrieved successfully.", "data", writeLesson, "currentPage", pageLessons.getNumber(), "totalItems", pageLessons.getTotalElements(), "totalPages", pageLessons.getTotalPages()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "An error occurred while fetching the lessons: " + e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createWritingLesson(@ModelAttribute @Valid Writing_LessonCreate writing_lesson) {
        if (writing_lesson.getFile() == null || writing_lesson.getFile().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", "Please choose a file to upload"));
        }
        try {
            writingLessonService.createLesson(writing_lesson.getTitle(), writing_lesson.getFile(), writing_lesson.getLevelId());
            return ResponseEntity.ok(Map.of("status", "success", "message", "Lesson has been created successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> detailWritingLesson(@PathVariable("id") Long writing_lessonId) {
        Optional<Writing_lesson> writing_lesson = writingLessonService.getWritingLessonById(writing_lessonId);
        try {
            if (writing_lesson.isEmpty()) {
                return ResponseEntity.ok(Map.of("status", "error", "message", "This lesson not exists"));
            }

            // Không nên chuyển đổi từ filePath về lines luôn vì sẽ nặng xử lý cho server
            // Trả về phản hồi ngay cho client
            return ResponseEntity.ok(Map.of("status", "success", "message", "Writing lessons retrieved successfully", "data", writing_lesson));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
