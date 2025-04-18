package Project.TiengAnhMoiNgay.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subtitle_lines")
public class Subtitle_lines {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String startTime;
    private String endTime;
    private String content;

    // Mối quan hệ ManyToOne với Listening_lessons
    @ManyToOne
    @JoinColumn(name = "lesson_id") // Tạo khóa ngoại
    @JsonBackReference // Ngăn không cho vòng lặp trong JSON
    private Listening_lessons lesson;
}
