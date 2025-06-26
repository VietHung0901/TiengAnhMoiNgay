package Project.TiengAnhMoiNgay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "writing_lesson")
public class Writing_lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String title;

    private String filePath;

    private String status;

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
}
