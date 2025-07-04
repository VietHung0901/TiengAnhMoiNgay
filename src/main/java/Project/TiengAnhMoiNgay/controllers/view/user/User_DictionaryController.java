package Project.TiengAnhMoiNgay.controllers.view.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/view/user/dictionary")
@RequiredArgsConstructor
public class User_DictionaryController {
    @GetMapping("/search")
    public String searchVocabulary(@RequestParam(name = "query", required = false) String query, Model model) {
        model.addAttribute("query", query); // truyền query xuống view (nếu bạn muốn sử dụng trong HTML/JS)
        return "user/dictionary/search";
    }

    @GetMapping("/translate")
    public String translate(@RequestParam(name = "query", required = false) String query, Model model) {
        model.addAttribute("query", query); // truyền query xuống view (nếu bạn muốn sử dụng trong HTML/JS)
        return "user/dictionary/translate";
    }
}