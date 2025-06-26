package Project.TiengAnhMoiNgay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lesson_type")
public class LessonType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String typeName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Writing_lesson> listWritingLesson;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Listening_lesson> listListeningLesson;

    @OneToMany(mappedBy = "lessonType", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<LearningLog> listLearningLog;
}
