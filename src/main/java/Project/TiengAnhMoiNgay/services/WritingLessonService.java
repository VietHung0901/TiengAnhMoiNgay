package Project.TiengAnhMoiNgay.services;

import Project.TiengAnhMoiNgay.entities.Level;
import Project.TiengAnhMoiNgay.entities.Listening_lesson;
import Project.TiengAnhMoiNgay.entities.Writing_lesson;
import Project.TiengAnhMoiNgay.model.StringURL;
import Project.TiengAnhMoiNgay.repositories.ILessonTypeRepository;
import Project.TiengAnhMoiNgay.repositories.ILevelRepository;
import Project.TiengAnhMoiNgay.repositories.IWritingLessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WritingLessonService {

    private final IWritingLessonRepository writingLessonRepository;
    private final ILevelRepository levelRepository;
    private final ILessonTypeRepository lessonTypeRepository;

    public Page<Writing_lesson> Pageable_WritingLessons(Pageable pageable, String status) {
        if (status == null)
            return writingLessonRepository.findAll(pageable);
        return writingLessonRepository.findByStatus(pageable, status);
    }

    public Optional<Writing_lesson> getWritingLessonById(Long id) {
        return writingLessonRepository.findById(id);
    }

    public String saveFile(MultipartFile file) {
        String uploadDir = StringURL.dirFilePath;
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
        return "/writings/" + fileName;
    }

    public void createLesson(String title, MultipartFile file, Long levelId) {
        try {
            Writing_lesson lesson = new Writing_lesson();
            lesson.setTitle(title);
            lesson.setFilePath(saveFile(file));
            lesson.setCategory(lessonTypeRepository.getLessonTypeById(2L));
            if (levelId != null) {
                Level level = levelRepository.findById(levelId)
                        .orElseThrow(() -> new RuntimeException("Level not found with ID: " + levelId));
                lesson.setLevel(level);
            }
            lesson.setStatus("done");
            writingLessonRepository.save(lesson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create writing lesson", e);
        }
    }

    public long getTotalWriting() {
        return writingLessonRepository.count();
    }

    public List<Writing_lesson> getLatestWritings() {
        return writingLessonRepository.findTop5ByOrderByCreatedAtDesc();
    }
}
