package Project.TiengAnhMoiNgay.repositories;

import Project.TiengAnhMoiNgay.entities.Listening_lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IListeningLessonRepository extends JpaRepository<Listening_lesson, Long> {
    Optional<Listening_lesson> findById(Long id);
    Listening_lesson findByYoutubeUrl(String youtubeUrl);
    boolean existsByYoutubeUrl(String youtubeUrl);
    Page<Listening_lesson> findAll(Pageable pageable);
    Page<Listening_lesson> findByStatus(Pageable pageable, String status);
}
