package Project.TiengAnhMoiNgay.repositories;

import Project.TiengAnhMoiNgay.entities.Reading_lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IReadingLessonRepository extends JpaRepository<Reading_lesson, Long> {
    Page<Reading_lesson> findAll(Pageable pageable);
    Page<Reading_lesson> findByStatus(Pageable pageable, String status);

    long count();

    List<Reading_lesson> findTop5ByOrderByCreatedAtDesc();

    Reading_lesson getReading_lessonById(Long Id);
}
