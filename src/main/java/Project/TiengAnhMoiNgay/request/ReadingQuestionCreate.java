package Project.TiengAnhMoiNgay.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ReadingQuestionCreate {
    private List<QuestionItem> questions;

    @Getter
    @Setter
    public static class QuestionItem {
        private String questionText;
        private List<ReadingOptionCreate> options;
    }
}
