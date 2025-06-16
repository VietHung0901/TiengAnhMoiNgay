package Project.TiengAnhMoiNgay.controllers.view.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/admin/writing")
@RequiredArgsConstructor
public class Writing_LessonsController {

    @GetMapping("/list/{pageNumber}")
    public String list(@PathVariable("pageNumber") int pageNumber) {
        return "/employee/writing/list";
    }

    @GetMapping("/create")
    public String create() {
        return "/employee/writing/create";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long writing_lessonId) {
        return "/employee/writing/detail";
    }
}
