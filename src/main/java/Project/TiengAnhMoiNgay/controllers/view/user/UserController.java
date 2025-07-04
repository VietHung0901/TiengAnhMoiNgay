package Project.TiengAnhMoiNgay.controllers.view.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/user")
@RequiredArgsConstructor
public class UserController {
    @GetMapping("/home")
    public String dashboard() {
        return "user/home";
    }
}
