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
@Table(name = "writing_lesson")
public class Writing_lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String title;

    private String filePath;

    private String status;

    @ManyToOne
    @JoinColumn(name = "level_id") // Tạo khóa ngoại
    @JsonIgnore
    private Level level;
}
