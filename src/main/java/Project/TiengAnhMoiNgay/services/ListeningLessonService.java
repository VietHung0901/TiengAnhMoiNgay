package Project.TiengAnhMoiNgay.services;

import Project.TiengAnhMoiNgay.entities.Listening_lessons;
import Project.TiengAnhMoiNgay.entities.Subtitle_lines;
import Project.TiengAnhMoiNgay.model.StringURL;
import Project.TiengAnhMoiNgay.repositories.IListeningLessonRepository;
import Project.TiengAnhMoiNgay.repositories.ISubtitleLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListeningLessonService {
    StringURL dir = new StringURL();

    private final IListeningLessonRepository lessonRepo;
    private final ISubtitleLineRepository subtitleRepo;

    private final YoutubeSubtitleService ytService;

    public boolean isYoutubeUrlExists(String youtubeUrl){
        return lessonRepo.existsByYoutubeUrl(youtubeUrl);
    }

    public List<Listening_lessons> getAllListeningLessons() {
        return lessonRepo.findAll();
    }

    public Page<Listening_lessons> Pageable_ListeningLessons(Pageable pageable) {
        return lessonRepo.findAll(pageable);
    }

    public Optional<Listening_lessons> getListeningLessonById(Long id) {
        return lessonRepo.findById(id);
    }

    @Async
    public void createLesson(String title, String youtubeUrl, String videoId) throws IOException, InterruptedException {
        try {
                // Tạo lesson với status là processing
                Listening_lessons lesson = Listening_lessons.builder().title(title).youtubeUrl(youtubeUrl).status("processing").build();

                lesson = lessonRepo.save(lesson);

                // Tải audio
                String audioPath = ytService.downloadAudioForWhisper(youtubeUrl, dir.getDirSubtitles_audio(), videoId);

                // Dùng Whisper để tạo file srt từ audio
                File srtFile = ytService.generateSRTWithWhisper(videoId, dir.getDirSubtitles_sub(), audioPath);

                // Parse phụ đề và gán vào lesson
                List<Subtitle_lines> lines = ytService.parseSrt(srtFile);

                // Lưu danh sách subline
                for (Subtitle_lines line : lines) {
                    line.setLesson(lesson);
                }
                subtitleRepo.saveAll(lines);

                // Cập nhập status done
                lesson.setLines(lines);
                lesson.setAudioUrl(audioPath);
                lesson.setStatus("done");
                lessonRepo.save(lesson);
        } catch (Exception e) {
            // Có thể lưu trạng thái lỗi vào DB nếu lesson đã được tạo
            try {
                Listening_lessons lessonError = lessonRepo.findByYoutubeUrl(youtubeUrl);
                if (lessonError != null) {
                    // Cập nhập status error
                    lessonError.setStatus("error - " + e.getMessage());
                    lessonRepo.save(lessonError);
                }
            } catch (Exception ignore) {
                // Tránh lỗi lồng nhau
            }
        }
    }
}
