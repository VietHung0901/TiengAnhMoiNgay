package Project.TiengAnhMoiNgay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subtitle_line")
public class Subtitle_line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String startTime;
    private String endTime;

    private String content;

    // Mối quan hệ ManyToOne với Listening_lessons
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false) // Tạo khóa ngoại
    @JsonIgnore
    private Listening_lesson lesson;
}
