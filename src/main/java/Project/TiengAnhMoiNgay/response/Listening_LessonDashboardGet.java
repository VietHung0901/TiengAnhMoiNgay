package Project.TiengAnhMoiNgay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class Listening_LessonDashboardGet {
    private Long Id;
    private String title;
    private String category;
    private LocalDate createdAt;
}
