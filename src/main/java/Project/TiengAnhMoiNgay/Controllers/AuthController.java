package Project.TiengAnhMoiNgay.Controllers;

import Project.TiengAnhMoiNgay.Entities.Role;
import Project.TiengAnhMoiNgay.Entities.User;
import Project.TiengAnhMoiNgay.Model.JWTTokenUtil;
import Project.TiengAnhMoiNgay.Model.StringURL;
import Project.TiengAnhMoiNgay.Request.UserLogin;
import Project.TiengAnhMoiNgay.Request.UserRegister;
import Project.TiengAnhMoiNgay.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    StringURL url = new StringURL();
    private final UserService userService;
    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(Model model) {
        System.out.println(url.getHttp());
        model.addAttribute("baseUrl", url.getHttp());
        return "Account/login";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogin userLogin) {
        // Kiểm tra tên người dùng
        User user = userService.findByUsername(userLogin.getUsername());
        if (user == null) {
            return createErrorResponse("Invalid username or password");
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
            return createErrorResponse("Invalid username or password");
        }

        // Lấy danh sách các vai trò của người dùng
        Set<Role> roles = user.getRoles();

        // Lấy vai trò đầu tiên trong tập hợp vai trò
        String roleName = roles.iterator().next().getName();

        // Tạo JWT token
        String token = jwtTokenUtil.generateToken(user);

        // Trả về thông tin JSON chuẩn
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Login successful",
                "data", Map.of(
                        "token", "Bearer " + token,
                        "username", user.getUsername(),
                        "role", roleName
                )
        ));
    }

    // Tạo phản hồi lỗi
    private ResponseEntity<?> createErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("status", "error", "message", message));
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("baseUrl", url.getHttp());
        return "Account/register";
    }

    @PostMapping("/register")
    public ResponseEntity<?> Register(@RequestBody UserRegister userRegister) {
        try {
            userService.createNewUser(userRegister);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Tạo tài khoản thành công."
            ));
        } catch (Exception e) {
            // Xử lý lỗi và trả về phản hồi thất bại
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Tạo tài khoản thất bại.",
                    "error", e.getMessage()
            ));
        }
    }
}
