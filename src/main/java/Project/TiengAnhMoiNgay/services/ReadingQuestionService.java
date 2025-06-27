package Project.TiengAnhMoiNgay.services;

import Project.TiengAnhMoiNgay.entities.ReadingOption;
import Project.TiengAnhMoiNgay.entities.ReadingQuestion;
import Project.TiengAnhMoiNgay.entities.Reading_lesson;
import Project.TiengAnhMoiNgay.repositories.IReadingLessonRepository;
import Project.TiengAnhMoiNgay.repositories.IReadingOptionRepository;
import Project.TiengAnhMoiNgay.repositories.IReadingQuestionRepository;
import Project.TiengAnhMoiNgay.request.ReadingQuestionCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadingQuestionService {
    private final IReadingLessonRepository lessonRepository;
    private final IReadingQuestionRepository questionRepository;

    @Async
    public void createQuestionWithOptions(ReadingQuestionCreate req, Reading_lesson lesson) {
        try {
            for (ReadingQuestionCreate.QuestionItem item : req.getQuestions()) {
                ReadingQuestion question = new ReadingQuestion();
                question.setReadingLesson(lesson);
                question.setQuestionText(item.getQuestionText());

                List<ReadingOption> options = item.getOptions().stream().map(opt -> {
                    ReadingOption o = new ReadingOption();
                    o.setOptionText(opt.getOptionText());
                    o.setTrue(opt.isTrue());
                    o.setQuestion(question);
                    return o;
                }).toList();

                question.setOptions(options);
                questionRepository.save(question);
                lesson.setStatus("done");
                lessonRepository.save(lesson);
            }
        } catch (Exception e) {
            lesson.setStatus("error");
            lesson.setErrorMessage(e.getMessage());
            lessonRepository.save(lesson);
        }
    }
}
