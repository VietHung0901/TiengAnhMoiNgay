package Project.TiengAnhMoiNgay.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLogin {
    @NotBlank(message = "Email cannot be blank")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
