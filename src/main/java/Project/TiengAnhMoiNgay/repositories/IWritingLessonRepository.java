package Project.TiengAnhMoiNgay.repositories;

import Project.TiengAnhMoiNgay.entities.Writing_lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IWritingLessonRepository extends JpaRepository<Writing_lesson, Long> {
    Page<Writing_lesson> findAll(Pageable pageable);
    Page<Writing_lesson> findByStatus(Pageable pageable, String status);

    long count();

    List<Writing_lesson> findTop5ByOrderByCreatedAtDesc();

    Writing_lesson getWriting_lessonById(Long Id);
}
