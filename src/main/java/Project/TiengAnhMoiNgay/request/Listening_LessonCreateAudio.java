package Project.TiengAnhMoiNgay.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class Listening_LessonCreateAudio {
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotNull(message = "Audio file is required")
    private MultipartFile audioFile;
    private Long levelId;
}