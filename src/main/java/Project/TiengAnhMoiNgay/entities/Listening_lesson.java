package Project.TiengAnhMoiNgay.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "listening_lesson")
public class Listening_lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String title;

    private String youtubeUrl;

    private String audioUrl;

    private String status;

    private String errorMessage;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Subtitle_line> lines;

    @ManyToOne
    @JoinColumn(name = "level_id") // Tạo khóa ngoại
    @JsonIgnore
    private Level level;
}
