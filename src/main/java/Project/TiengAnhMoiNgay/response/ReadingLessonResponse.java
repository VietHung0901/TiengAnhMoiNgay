package Project.TiengAnhMoiNgay.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReadingLessonResponse {
    private Long id;
    private String title;
    private String filePath;
    private List<QuestionResponse> questions;

    @Getter
    @Setter
    public static class QuestionResponse {
        private Long id;
        private String questionText;
        private List<OptionResponse> options;
    }

    @Getter
    @Setter
    public static class OptionResponse {
        private Long id;
        private String optionText;
        private boolean isTrue;
    }
}
