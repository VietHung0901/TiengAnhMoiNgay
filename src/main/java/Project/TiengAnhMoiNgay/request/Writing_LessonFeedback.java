package Project.TiengAnhMoiNgay.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Writing_LessonFeedback {
    private String viSentence;   // Câu tiếng Việt (đề bài)
    private String userAnswer;   // Câu trả lời của học sinh
}
