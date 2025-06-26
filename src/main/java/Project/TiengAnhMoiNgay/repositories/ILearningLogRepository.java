package Project.TiengAnhMoiNgay.repositories;

import Project.TiengAnhMoiNgay.entities.LearningLog;
import Project.TiengAnhMoiNgay.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ILearningLogRepository extends JpaRepository<LearningLog, Long> {

    List<LearningLog> findByUser(User user);
    // Kiểm tra xem có log nào của user học bài đó gần đây không (để tránh spam log)
    boolean existsByUserIdAndLessonIdAndViewedAtAfter(Long userId, Long lessonId, LocalDateTime afterTime);

    @Query("SELECT DATE(l.viewedAt), COUNT(l) FROM LearningLog l " +
            "WHERE l.viewedAt BETWEEN :start AND :end " +
            "GROUP BY DATE(l.viewedAt) " +
            "ORDER BY DATE(l.viewedAt)")
    List<Object[]> countLearningByDay(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
