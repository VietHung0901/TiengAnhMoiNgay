package Project.TiengAnhMoiNgay.controllers.api;

import Project.TiengAnhMoiNgay.entities.LearningLog;
import Project.TiengAnhMoiNgay.entities.Level;
import Project.TiengAnhMoiNgay.entities.Listening_lesson;
import Project.TiengAnhMoiNgay.entities.User;
import Project.TiengAnhMoiNgay.model.StringURL;
import Project.TiengAnhMoiNgay.repositories.ILearningLogRepository;
import Project.TiengAnhMoiNgay.repositories.ILessonTypeRepository;
import Project.TiengAnhMoiNgay.repositories.ILevelRepository;
import Project.TiengAnhMoiNgay.repositories.IListeningLessonRepository;
import Project.TiengAnhMoiNgay.request.Listening_LessonCreateAudio;
import Project.TiengAnhMoiNgay.request.Listening_LessonCreateLink;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/listening_lesson")
@RequiredArgsConstructor
public class ApiListening_LessonController {

    private final ListeningLessonService lessonService;
    private final YoutubeSubtitleService ytService;
    private final ILevelRepository levelRepository;
    private final IListeningLessonRepository lessonRepo;
    private final ILearningLogRepository learningLogRepository;
    private final ILessonTypeRepository lessonTypeRepository;

    @GetMapping("/list/{pageNumber}")
    public ResponseEntity<?> listListeningLesson(@PathVariable("pageNumber") int pageNumber, @RequestParam(value = "status", required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, 10);
            Page<Listening_lesson> pageLessons = lessonService.Pageable_ListeningLessons(pageable, status);

            List<Listening_LessonGet> listLesson = pageLessons.stream().map(lesson -> Listening_LessonGet.builder().Id(lesson.getId()).title(lesson.getTitle()).level(lesson.getLevel().getName()).youtubeUrl(lesson.getYoutubeUrl()).status(lesson.getStatus()).build()).toList();

            return ResponseEntity.ok(Map.of("status", "success", "message", "List of listening lessons retrieved successfully.", "data", listLesson, "currentPage", pageLessons.getNumber(), "totalItems", pageLessons.getTotalElements(), "totalPages", pageLessons.getTotalPages()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "An error occurred while fetching the lessons: " + e.getMessage()));
        }
    }

    @PostMapping("/create/link")
    public ResponseEntity<?> createListeningLessonLink(@RequestBody @Valid Listening_LessonCreateLink listening_lesson) {

        String youtubeUrl = listening_lesson.getYoutubeUrl(), title = listening_lesson.getTitle();

        if (lessonService.isYoutubeUrlExists(youtubeUrl)) {
            return ResponseEntity.ok(Map.of("status", "error", "message", "This lesson already exists"));
        }

        // Kiểm tra url cung cấp có tồn tại video youtube không
        if (!ytService.isYoutubeVideoExists(youtubeUrl)) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Video not found or not accessible"));
        }

        youtubeUrl = youtubeUrl.split("&")[0];
        String videoId = ytService.extractVideoId(youtubeUrl);

        try {
            Level level = levelRepository.findById(listening_lesson.getLevelId())
                    .orElseThrow(() -> new RuntimeException("Level not found with ID: " + listening_lesson.getLevelId()));
            // Tạo lesson với status là processing
            Listening_lesson lesson = Listening_lesson.builder().title(title).level(level).youtubeUrl(youtubeUrl).status("processing").category(lessonTypeRepository.getLessonTypeById(1L)).build();

            lesson = lessonRepo.save(lesson);

            // Gọi bất đồng bộ
            lessonService.createLessonLink(lesson, youtubeUrl, videoId);
            // Trả về phản hồi ngay cho client
            return ResponseEntity.ok(Map.of("status", "success", "message", "Lesson is being generated in background. Please check status later."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/create/audio")
    public ResponseEntity<?> createListeningLessonAudio(@ModelAttribute @Valid Listening_LessonCreateAudio listening_lesson) {
        try {
            Level level = levelRepository.findById(listening_lesson.getLevelId())
                    .orElseThrow(() -> new RuntimeException("Level not found with ID: " + listening_lesson.getLevelId()));
            // Tạo lesson với status là processing
            Listening_lesson lesson = Listening_lesson.builder().title(listening_lesson.getTitle()).level(level).status("processing").category(lessonTypeRepository.getLessonTypeById(1L)).build();
            lesson = lessonRepo.save(lesson);

            // Gọi bất đồng bộ
            lessonService.createLessonAudio(lesson, listening_lesson.getAudioFile(), listening_lesson.getAudioFile().getOriginalFilename());
            // Trả về phản hồi ngay cho client
            return ResponseEntity.ok(Map.of("status", "success", "message", "Lesson is being generated in background. Please check status later."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> detailListeningLesson(@PathVariable("id") Long listening_lessonId,
                                                   @AuthenticationPrincipal User user) {
        Optional<Listening_lesson> listening_lesson = lessonService.getListeningLessonById(listening_lessonId);
        try {
            if (listening_lesson.isEmpty()) {
                return ResponseEntity.ok(Map.of("status", "error", "message", "This lesson not exists"));
            }

            boolean isUser = user.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("USER"));

            boolean isRecentlyLogged = learningLogRepository.existsByUserIdAndLessonIdAndViewedAtAfter(
                    user.getId(),
                    listening_lessonId,
                    LocalDateTime.now().minusMinutes(30)
            );

            if (isUser && !isRecentlyLogged) {
                LearningLog log = new LearningLog();
                log.setUser(user);
                log.setLessonId(listening_lessonId);
                log.setLessonType(lessonTypeRepository.getLessonTypeById(1L));
                log.setViewedAt(LocalDateTime.now());
                learningLogRepository.save(log);
            }

            // Trả về phản hồi ngay cho client
            return ResponseEntity.ok(Map.of("status", "success", "message", "Listening lessons retrieved successfully", "data", listening_lesson));
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
                return ResponseEntity.status(400).body(Map.of("status", "error", "message", "The recording file could not be sent"));
            }

            // Lưu tệp ghi âm tạm thời vào thư mục tạm
            String tempDir = StringURL.dirSubtitles_audio;
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
            File srtFile = ytService.generateSRTWithWhisper(nameTempAudioFile, StringURL.dirSubtitles_sub, audioPath);

            // Parse phụ đề và gán vào lesson
            String lines = ytService.parseSrtFromVoice(srtFile);

            // Xóa tệp tạm sau khi xử lý
            tempAudioFile.delete();
            srtFile.delete();

            // Trả về phản hồi ngay cho client
            return ResponseEntity.ok(Map.of("status", "success", "message", "Generate text from voice successlly!", "data", lines));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Error saving audio file: " + e.getMessage()));
        } catch (InterruptedException e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Error while processing Whisper: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Unknown error: " + e.getMessage()));
        }
    }
}
