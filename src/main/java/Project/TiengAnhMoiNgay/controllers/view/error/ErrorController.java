package Project.TiengAnhMoiNgay.controllers.view.error;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/error")
@RequiredArgsConstructor
public class ErrorController {

    @GetMapping("/403")
    public String login() {
        return "error/403";
    }
}
