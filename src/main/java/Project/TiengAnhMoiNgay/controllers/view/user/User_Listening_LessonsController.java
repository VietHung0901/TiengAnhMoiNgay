package Project.TiengAnhMoiNgay.controllers.view.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/user/listening")
@RequiredArgsConstructor
public class User_Listening_LessonsController {

    @GetMapping("/list/{pageNumber}")
    public String list(@PathVariable("pageNumber") int pageNumber) {
        return "/user/listening/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long listening_lessonId) {
        return "/user/listening/detail";
    }
}
