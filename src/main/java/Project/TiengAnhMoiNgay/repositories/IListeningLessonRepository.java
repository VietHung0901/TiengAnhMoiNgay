package Project.TiengAnhMoiNgay.repositories;

import Project.TiengAnhMoiNgay.entities.Listening_lessons;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IListeningLessonRepository extends JpaRepository<Listening_lessons, Long> {
    Optional<Listening_lessons> findById(Long id);
    Listening_lessons findByYoutubeUrl(String youtubeUrl);
    boolean existsByYoutubeUrl(String youtubeUrl);
    Page<Listening_lessons> findAll(Pageable pageable);
}
