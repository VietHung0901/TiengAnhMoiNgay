package Project.TiengAnhMoiNgay.controllers.view.employee;

import Project.TiengAnhMoiNgay.model.StringURL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/admin/listening")
@RequiredArgsConstructor
public class Listening_LessonsController {
    StringURL url = new StringURL();

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("baseUrl", url.getHttp());
        return "employee/listening/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("baseUrl", url.getHttp());
        return "employee/listening/create";
    }
}
