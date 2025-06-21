package Project.TiengAnhMoiNgay.services;

import Project.TiengAnhMoiNgay.entities.Level;
import Project.TiengAnhMoiNgay.entities.Listening_lesson;
import Project.TiengAnhMoiNgay.entities.Subtitle_line;
import Project.TiengAnhMoiNgay.model.StringURL;
import Project.TiengAnhMoiNgay.repositories.ILevelRepository;
import Project.TiengAnhMoiNgay.repositories.IListeningLessonRepository;
import Project.TiengAnhMoiNgay.repositories.ISubtitleLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListeningLessonService {

    private final IListeningLessonRepository lessonRepo;
    private final ISubtitleLineRepository subtitleRepo;
    private final YoutubeSubtitleService ytService;

    public boolean isYoutubeUrlExists(String youtubeUrl) {
        return lessonRepo.existsByYoutubeUrl(youtubeUrl);
    }

    public List<Listening_lesson> getAllListeningLessons() {
        return lessonRepo.findAll();
    }

    public Page<Listening_lesson> Pageable_ListeningLessons(Pageable pageable, String status) {
        if (status == null)
            return lessonRepo.findAll(pageable);
        return lessonRepo.findByStatus(pageable, status);
    }

    public Optional<Listening_lesson> getListeningLessonById(Long id) {
        return lessonRepo.findById(id);
    }

    public static String extractRelativePath(String fullPath) {
        String target = "/subtitles/audios/";
        int index = fullPath.indexOf(target);
        if (index != -1) {
            return fullPath.substring(index);
        } else {
            return null;
        }
    }

    @Async
    public void createLessonLink(Listening_lesson lesson, String youtubeUrl, String videoId) throws IOException, InterruptedException {
        try {
            // Tải audio
            String audioPath = ytService.downloadAudioForWhisper(youtubeUrl, StringURL.dirSubtitles_audio, videoId);

            // Dùng Whisper để tạo file srt từ audio
            File srtFile = ytService.generateSRTWithWhisper(videoId, StringURL.dirSubtitles_sub, audioPath);

            // Parse phụ đề và gán vào lesson
            List<Subtitle_line> lines = ytService.parseSrt(srtFile);

            srtFile.delete();

            // Lưu danh sách subline
            for (Subtitle_line line : lines) {
                line.setLesson(lesson);
            }
            subtitleRepo.saveAll(lines);

            // Cập nhập status done
            lesson.setLines(lines);
            lesson.setAudioUrl(extractRelativePath(audioPath));
            lesson.setStatus("done");
            lessonRepo.save(lesson);
        } catch (Exception e) {
            // Có thể lưu trạng thái lỗi vào DB nếu lesson đã được tạo
            try {
                // Cập nhập status error
                lesson.setStatus("error");
                lesson.setErrorMessage(e.getMessage());
                lessonRepo.save(lesson);
            } catch (Exception ignore) {
                // Tránh lỗi lồng nhau
            }
        }
    }

    @Async
    public void createLessonAudio(Listening_lesson lesson, MultipartFile audioFile, String filename) throws IOException, InterruptedException {
        try {
            // Tải audio
            String audioPath = ytService.saveAudio(audioFile);

            filename = removeMp3Extension(filename);

            // Dùng Whisper để tạo file srt từ audio
            File srtFile = ytService.generateSRTWithWhisper(filename, StringURL.dirSubtitles_sub, audioPath);

            // Parse phụ đề và gán vào lesson
            List<Subtitle_line> lines = ytService.parseSrt(srtFile);

            srtFile.delete();

            // Lưu danh sách subline
            for (Subtitle_line line : lines) {
                line.setLesson(lesson);
            }
            subtitleRepo.saveAll(lines);

            // Cập nhập status done
            lesson.setLines(lines);
            lesson.setAudioUrl(extractRelativePath(audioPath));
            lesson.setStatus("done");
            lessonRepo.save(lesson);
        } catch (Exception e) {
            // Có thể lưu trạng thái lỗi vào DB nếu lesson đã được tạo
            try {
                // Cập nhập status error
                lesson.setStatus("error");
                lesson.setErrorMessage(e.getMessage());
                lessonRepo.save(lesson);
            } catch (Exception ignore) {
                // Tránh lỗi lồng nhau
            }
        }
    }

    public String removeMp3Extension(String fileName) {
        if (fileName != null && fileName.toLowerCase().endsWith(".mp3")) {
            return fileName.replaceFirst("(?i)\\.mp3$", "");
        }
        return fileName;
    }
}
