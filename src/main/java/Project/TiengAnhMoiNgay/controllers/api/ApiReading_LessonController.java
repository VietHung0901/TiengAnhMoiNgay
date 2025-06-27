package Project.TiengAnhMoiNgay.controllers.api;

import Project.TiengAnhMoiNgay.entities.LearningLog;
import Project.TiengAnhMoiNgay.entities.Reading_lesson;
import Project.TiengAnhMoiNgay.entities.User;
import Project.TiengAnhMoiNgay.repositories.ILearningLogRepository;
import Project.TiengAnhMoiNgay.repositories.ILessonTypeRepository;
import Project.TiengAnhMoiNgay.repositories.IReadingLessonRepository;
import Project.TiengAnhMoiNgay.request.ReadingQuestionCreate;
import Project.TiengAnhMoiNgay.request.Reading_LessonCreate;
import Project.TiengAnhMoiNgay.response.ReadingLessonResponse;
import Project.TiengAnhMoiNgay.response.Reading_LessonGet;
import Project.TiengAnhMoiNgay.services.ReadingLessonService;
import Project.TiengAnhMoiNgay.services.ReadingQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reading_lesson")
@RequiredArgsConstructor
public class ApiReading_LessonController {

    private final ReadingLessonService readingLessonService;
    private final ReadingQuestionService questionService;
    private final IReadingLessonRepository readingLessonRepository;
    private final ILearningLogRepository learningLogRepository;
    private final ILessonTypeRepository lessonTypeRepository;

    @GetMapping("/list/{pageNumber}")
    public ResponseEntity<?> listReadingLesson(@PathVariable("pageNumber") int pageNumber, @RequestParam(value = "status", required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, 10);
            Page<Reading_lesson> pageLessons = readingLessonService.Pageable_ReadingLessons(pageable, status);

            List<Reading_LessonGet> writeLesson = pageLessons.stream().map(lesson -> Reading_LessonGet.builder().Id(lesson.getId()).title(lesson.getTitle()).level(lesson.getLevel().getName()).status(lesson.getStatus()).build()).toList();

            return ResponseEntity.ok(Map.of("status", "success", "message", "List of reading lessons retrieved successfully.", "data", writeLesson, "currentPage", pageLessons.getNumber(), "totalItems", pageLessons.getTotalElements(), "totalPages", pageLessons.getTotalPages()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "An error occurred while fetching the lessons: " + e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReadingLesson(@ModelAttribute @Valid Reading_LessonCreate reading_lesson, @RequestPart("questions") ReadingQuestionCreate request) {
        if (reading_lesson.getFile() == null || reading_lesson.getFile().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", "Please choose a file to upload"));
        }
        try {
            Reading_lesson lesson = readingLessonService.createLesson(reading_lesson.getTitle(), reading_lesson.getFile(), reading_lesson.getLevelId());

            // Gọi bất đồng bộ
            questionService.createQuestionWithOptions(request, lesson);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Lesson is being generated in background. Please check status later."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getReadingLessonDetail(@PathVariable("id") Long reading_lessonId,
                                                    @AuthenticationPrincipal User user) {
        try{
            Reading_lesson lesson = readingLessonRepository.findById(reading_lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));
            ReadingLessonResponse res = new ReadingLessonResponse();
            res.setId(lesson.getId());
            res.setTitle(lesson.getTitle());
            res.setFilePath(lesson.getFilePath());

            List<ReadingLessonResponse.QuestionResponse> questionList = lesson.getQuestions().stream().map(q -> {
                ReadingLessonResponse.QuestionResponse qr = new ReadingLessonResponse.QuestionResponse();
                qr.setId(q.getId());
                qr.setQuestionText(q.getQuestionText());

                List<ReadingLessonResponse.OptionResponse> optionList = q.getOptions().stream().map(o -> {
                    ReadingLessonResponse.OptionResponse opt = new ReadingLessonResponse.OptionResponse();
                    opt.setId(o.getId());
                    opt.setOptionText(o.getOptionText());
                    opt.setTrue(o.isTrue());
                    return opt;
                }).toList();

                qr.setOptions(optionList);
                return qr;
            }).toList();

            res.setQuestions(questionList);

            // Lưu LearningLog
            boolean isUser = user.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("USER"));

            boolean isRecentlyLogged = learningLogRepository.existsByUserIdAndLessonIdAndViewedAtAfter(
                    user.getId(),
                    reading_lessonId,
                    LocalDateTime.now().minusMinutes(30)
            );

            if (isUser && !isRecentlyLogged) {
                LearningLog log = new LearningLog();
                log.setUser(user);
                log.setLessonId(reading_lessonId);
                log.setLessonType(lessonTypeRepository.getLessonTypeById(3L));
                log.setViewedAt(LocalDateTime.now());
                learningLogRepository.save(log);
            }

            return ResponseEntity.ok(Map.of("status", "success", "message", "Reading lessons retrieved successfully", "data", res));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
