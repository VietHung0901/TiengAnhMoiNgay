package Project.TiengAnhMoiNgay.controllers.view.employee;

import Project.TiengAnhMoiNgay.model.StringURL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/view/auth")
@RequiredArgsConstructor
public class AuthController {
    StringURL url = new StringURL();

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("baseUrl", url.getHttp());
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("baseUrl", url.getHttp());
        return "auth/register";
    }
}
