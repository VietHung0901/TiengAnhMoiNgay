package Project.TiengAnhMoiNgay.controllers.view.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/admin/reading")
@RequiredArgsConstructor
public class Reading_LessonsController {

    @GetMapping("/list/{pageNumber}")
    public String list(@PathVariable("pageNumber") int pageNumber) {
        return "employee/reading/list";
    }

    @GetMapping("/create")
    public String create() {
        return "employee/reading/create";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long reading_lessonId) {
        return "employee/reading/detail";
    }

}
