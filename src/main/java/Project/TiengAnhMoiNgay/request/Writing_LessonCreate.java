package Project.TiengAnhMoiNgay.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class Writing_LessonCreate {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    private MultipartFile file;

    private Long levelId;
}
