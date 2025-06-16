package Project.TiengAnhMoiNgay.controllers.view.employee;

import Project.TiengAnhMoiNgay.model.StringURL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/admin")
@RequiredArgsConstructor
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "/employee/dashboard";
    }
}
