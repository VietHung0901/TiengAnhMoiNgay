package Project.TiengAnhMoiNgay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class Writing_LessonGet {
    private Long Id;
    private String title;
    private String level;
    private String status;
}
