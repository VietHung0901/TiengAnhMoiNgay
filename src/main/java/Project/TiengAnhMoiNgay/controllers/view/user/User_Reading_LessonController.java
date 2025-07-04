package Project.TiengAnhMoiNgay.controllers.view.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/user/reading")
@RequiredArgsConstructor
public class User_Reading_LessonController {

    @GetMapping("/list/{pageNumber}")
    public String list(@PathVariable("pageNumber") int pageNumber) {
        return "user/reading/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long reading_lessonId) {
        return "user/reading/detail";
    }

}
