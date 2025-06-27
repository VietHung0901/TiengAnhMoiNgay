package Project.TiengAnhMoiNgay.entities;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reading_question")
public class ReadingQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reading_lesson_id", nullable = false)
    private Reading_lesson readingLesson;

    private String questionText;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<ReadingOption> options;
}
