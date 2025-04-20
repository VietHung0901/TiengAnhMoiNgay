package Project.TiengAnhMoiNgay.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "listening_lessons")
public class Listening_lessons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String title;

    private String youtube_url;

    private String audio_path;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private List<Subtitle_lines> lines;
}
