package Project.TiengAnhMoiNgay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reading_lesson")
public class Reading_lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String filePath;

    private String status;

    private String errorMessage;

    @ManyToOne
    @JoinColumn(name = "lessonType_id") // Tạo khóa ngoại
    @JsonIgnore
    private LessonType category;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }

    @ManyToOne
    @JoinColumn(name = "level_id") // Tạo khóa ngoại
    @JsonIgnore
    private Level level;

    @OneToMany(mappedBy = "readingLesson", cascade = CascadeType.ALL)
    private List<ReadingQuestion> questions;
}
