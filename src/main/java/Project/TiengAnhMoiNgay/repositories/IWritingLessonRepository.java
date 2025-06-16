package Project.TiengAnhMoiNgay.repositories;

import Project.TiengAnhMoiNgay.entities.Listening_lesson;
import Project.TiengAnhMoiNgay.entities.Writing_lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWritingLessonRepository extends JpaRepository<Writing_lesson, Long> {
    Page<Writing_lesson> findAll(Pageable pageable);
    Page<Writing_lesson> findByStatus(Pageable pageable, String status);
}
