package Project.TiengAnhMoiNgay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class Listening_LessonGet {
    private Long Id;
    private String title;
    private String youtubeUrl;
    private String status;
}
