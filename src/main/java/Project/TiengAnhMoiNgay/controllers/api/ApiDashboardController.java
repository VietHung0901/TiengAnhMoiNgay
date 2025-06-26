package Project.TiengAnhMoiNgay.controllers.api;

import Project.TiengAnhMoiNgay.entities.LearningLog;
import Project.TiengAnhMoiNgay.entities.Listening_lesson;
import Project.TiengAnhMoiNgay.entities.User;
import Project.TiengAnhMoiNgay.entities.Writing_lesson;
import Project.TiengAnhMoiNgay.repositories.ILearningLogRepository;
import Project.TiengAnhMoiNgay.repositories.IListeningLessonRepository;
import Project.TiengAnhMoiNgay.repositories.IWritingLessonRepository;
import Project.TiengAnhMoiNgay.response.Listening_LessonDashboardGet;
import Project.TiengAnhMoiNgay.response.Listening_LessonGet;
import Project.TiengAnhMoiNgay.response.UserDashboardAminGet;
import Project.TiengAnhMoiNgay.response.Writing_LessonDashboardGet;
import Project.TiengAnhMoiNgay.services.ListeningLessonService;
import Project.TiengAnhMoiNgay.services.UserService;
import Project.TiengAnhMoiNgay.services.WritingLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class ApiDashboardController {
    private final UserService userService;
    private final ListeningLessonService listeningLessonService;
    private final IListeningLessonRepository listeningLessonRepository;
    private final WritingLessonService writingLessonService;
    private final IWritingLessonRepository writingLessonRepository;
    private final ILearningLogRepository learningLogRepository;

    @GetMapping("/admin")
    public ResponseEntity<?> listListeningLesson() {
        try {
            long userCount = userService.getTotalUsers();
            List<User> listUsers = userService.getLatestUsers();
            List<UserDashboardAminGet> latestUsers = listUsers.stream().map(user -> UserDashboardAminGet.builder().name(user.getName()).createdAt(user.getCreatedAt()).build()).toList();

            long listeningCount = listeningLessonService.getTotalListening();
            List<Listening_lesson> listListenings = listeningLessonService.getLatestListenings();
            List<Listening_LessonDashboardGet> latestListenings = listListenings.stream().map(listening -> Listening_LessonDashboardGet.builder().Id(listening.getId()).title(listening.getTitle()).createdAt(listening.getCreatedAt()).category(listening.getCategory().getTypeName()).build()).toList();

            long writingCount = writingLessonService.getTotalWriting();
            List<Writing_lesson> listWritings = writingLessonService.getLatestWritings();
            List<Writing_LessonDashboardGet> latestWritings = listWritings.stream().map(writing -> Writing_LessonDashboardGet.builder().Id(writing.getId()).title(writing.getTitle()).createdAt(writing.getCreatedAt()).category(writing.getCategory().getTypeName()).build()).toList();

            return ResponseEntity.ok(Map.of("status", "success", "message", "Dashboard admin successfully.", "userCount", userCount, "latestUsers", latestUsers, "writingCount", writingCount, "listeningCount", listeningCount, "latestListenings", latestListenings, "latestWritings", latestWritings));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "An error occurred while fetching: " + e.getMessage()));
        }
    }

    @GetMapping("/learning-activity")
    public ResponseEntity<?> getLearningActivity() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDate sevenDaysAgo = today.minusDays(6); // từ hôm nay lùi về 6 ngày trước

        List<Object[]> logs = learningLogRepository.countLearningByDay(sevenDaysAgo.atStartOfDay(), now);

        // Chuẩn bị map với 7 ngày gần nhất (đảm bảo đủ 7 ngày)
        Map<String, Integer> activityMap = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            activityMap.put(date.getDayOfWeek().toString().substring(0, 3), 0); // eg: MON, TUE...
        }

        // Lặp kết quả từ DB và cập nhật map
        for (Object[] row : logs) {
            java.sql.Date sqlDate = (java.sql.Date) row[0];
            LocalDate date = sqlDate.toLocalDate();
            Long count = (Long) row[1];
            String day = date.getDayOfWeek().toString().substring(0, 3);
            activityMap.put(day, count.intValue());
        }


        return ResponseEntity.ok(Map.of(
                "status", "success",
                "labels", activityMap.keySet(),
                "data", activityMap.values()
        ));
    }

    @GetMapping("/home")
    public ResponseEntity<?> getLearningLogsByUser(@AuthenticationPrincipal User user) {
        try {
            List<LearningLog> logs = learningLogRepository.findByUser(user);

            // Dùng LinkedHashMap để giữ thứ tự và loại trùng
            Map<String, LearningLog> uniqueLogs = new LinkedHashMap<>();

            logs.stream()
                    .sorted((a, b) -> b.getViewedAt().compareTo(a.getViewedAt())) // Sắp xếp giảm dần
                    .forEach(log -> {
                        String key = log.getLessonId() + "-" + log.getLessonType().getId(); // Key duy nhất cho mỗi bài học
                        if (!uniqueLogs.containsKey(key)) {
                            uniqueLogs.put(key, log);
                        }
                    });

            List<Map<String, Object>> response = uniqueLogs.values().stream()
                    .limit(5)
                    .map(log -> {
                        Map<String, Object> item = new HashMap<>();
                        Long lessonId = log.getLessonId();
                        item.put("lessonId", lessonId);
                        item.put("lessonType", log.getLessonType().getTypeName());

                        Long lessonTypeId = log.getLessonType().getId();
                        String title = "";

                        if (lessonTypeId == 1) {
                            title = listeningLessonRepository.getListening_lessonById(lessonId).getTitle();
                        } else if (lessonTypeId == 2) {
                            title = writingLessonRepository.getWriting_lessonById(lessonId).getTitle();
                        } else {
                            title = "reading";
                        }

                        item.put("title", title);
                        item.put("viewedAt", log.getViewedAt());
                        return item;
                    })
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

}
