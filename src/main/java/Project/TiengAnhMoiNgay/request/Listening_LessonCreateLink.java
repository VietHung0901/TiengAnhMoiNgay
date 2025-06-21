package Project.TiengAnhMoiNgay.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Listening_LessonCreateLink {
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotBlank(message = "Link cannot be blank")
    private String youtubeUrl;
    private Long levelId;
}
