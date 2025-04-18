package Project.TiengAnhMoiNgay.controllers.api;

import Project.TiengAnhMoiNgay.entities.Role;
import Project.TiengAnhMoiNgay.entities.User;
import Project.TiengAnhMoiNgay.model.JWTTokenUtil;
import Project.TiengAnhMoiNgay.request.UserLogin;
import Project.TiengAnhMoiNgay.request.UserRegister;
import Project.TiengAnhMoiNgay.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final UserService userService;

    private final JWTTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogin userLogin) {
        try {
            // Kiểm tra tên người dùng
            User user = userService.findByUsername(userLogin.getUsername());
            if (user == null || !passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "message", "Invalid username or password"));
            }

            // Lấy danh sách các vai trò của người dùng
            Set<Role> roles = user.getRoles();

            // Lấy vai trò đầu tiên trong tập hợp vai trò
            String roleName = roles.iterator().next().getName();

            // Tạo JWT token
            String token = jwtTokenUtil.generateToken(user);

            // Trả về thông tin JSON chuẩn
            return ResponseEntity.ok(Map.of("status", "success", "message", "Login successfully!", "data", Map.of("token", "Bearer " + token, "username", user.getUsername(), "role", roleName)));
        } catch (Exception e) {
            // Xử lý lỗi và trả về phản hồi thất bại
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> Register(@RequestBody @Valid UserRegister userRegister) {
        try {
            String result = userService.createNewUser(userRegister);
            if (result.equals("Success")) {
                return ResponseEntity.ok(Map.of("status", "success", "message", "Account created successfully!"));
            }
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", result));
        } catch (Exception e) {
            // Xử lý lỗi và trả về phản hồi thất bại
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Đăng xuất thành công"
            ));
        } catch (Exception e) {
            // Xử lý lỗi và trả về phản hồi thất bại
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

}
