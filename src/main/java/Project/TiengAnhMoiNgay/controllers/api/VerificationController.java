package Project.TiengAnhMoiNgay.controllers.api;

import Project.TiengAnhMoiNgay.repositories.IUserRepository;
import Project.TiengAnhMoiNgay.repositories.VerificationTokenRepository;
import Project.TiengAnhMoiNgay.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class VerificationController {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private IUserRepository userRepository;

    private final UserService userService;

//    @GetMapping("/confirm")
//    public String confirmEmail(@RequestParam("token") String token) {
//        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
//                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));
//
//        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
//            return "/Account/XacNhanEmail/fail";
//        }
//
//        User user = verificationToken.getUser();
//        user.setEnabled(true); // Kích hoạt tài khoản
//        userRepository.save(user);
//
//        return "/Account/XacNhanEmail/success";
//    }
}