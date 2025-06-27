package Project.TiengAnhMoiNgay.services;

import Project.TiengAnhMoiNgay.entities.Level;
import Project.TiengAnhMoiNgay.entities.Reading_lesson;
import Project.TiengAnhMoiNgay.entities.Writing_lesson;
import Project.TiengAnhMoiNgay.model.StringURL;
import Project.TiengAnhMoiNgay.repositories.ILessonTypeRepository;
import Project.TiengAnhMoiNgay.repositories.ILevelRepository;
import Project.TiengAnhMoiNgay.repositories.IReadingLessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadingLessonService {

    private final IReadingLessonRepository readingLessonRepository;
    private final ILevelRepository levelRepository;
    private final ILessonTypeRepository lessonTypeRepository;

    public Page<Reading_lesson> Pageable_ReadingLessons(Pageable pageable, String status) {
        if (status == null)
            return readingLessonRepository.findAll(pageable);
        return readingLessonRepository.findByStatus(pageable, status);
    }

    public String saveFile(MultipartFile file) {
        String uploadDir = StringURL.dirFilePathReading;
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lưu file vào đường dẫn
            try (InputStream inputStream = file.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file: " + fileName, e);
        }

        // Trả về đường dẫn tĩnh của file
        return "/readings/" + fileName;
    }

    public Reading_lesson createLesson(String title, MultipartFile file, Long levelId) {
        try {
            Reading_lesson lesson = new Reading_lesson();
            lesson.setTitle(title);
            lesson.setFilePath(saveFile(file));
            lesson.setCategory(lessonTypeRepository.getLessonTypeById(3L));
            if (levelId != null) {
                Level level = levelRepository.findById(levelId)
                        .orElseThrow(() -> new RuntimeException("Level not found with ID: " + levelId));
                lesson.setLevel(level);
            }
            lesson.setStatus("processing");
            readingLessonRepository.save(lesson);
            return lesson;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create reding lesson", e);
        }
    }

    public long getTotalReading() {
        return readingLessonRepository.count();
    }

    public List<Reading_lesson> getLatestReadings() {
        return readingLessonRepository.findTop5ByOrderByCreatedAtDesc();
    }
}
