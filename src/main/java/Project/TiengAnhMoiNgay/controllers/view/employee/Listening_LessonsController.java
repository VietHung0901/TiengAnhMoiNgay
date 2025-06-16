package Project.TiengAnhMoiNgay.controllers.view.employee;

import Project.TiengAnhMoiNgay.model.StringURL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/admin/listening")
@RequiredArgsConstructor
public class Listening_LessonsController {

    @GetMapping("/list/{pageNumber}")
    public String list(@PathVariable("pageNumber") int pageNumber) {
        return "/employee/listening/list";
    }

    @GetMapping("/create")
    public String create() {
        return "/employee/listening/create";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long listening_lessonId) {
        return "/employee/listening/detail";
    }
}
