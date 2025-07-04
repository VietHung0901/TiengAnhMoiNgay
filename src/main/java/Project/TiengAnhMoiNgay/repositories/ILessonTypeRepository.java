package Project.TiengAnhMoiNgay.repositories;

import Project.TiengAnhMoiNgay.entities.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILessonTypeRepository extends JpaRepository<LessonType, Long> {
    LessonType getLessonTypeById(Long Id);
}
