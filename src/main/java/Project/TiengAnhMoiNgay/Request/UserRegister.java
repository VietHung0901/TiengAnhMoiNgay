package Project.TiengAnhMoiNgay.Request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRegister {
    private String name;
    private String username;
    private String phone;
    private String password;
    private Integer gender;
    private LocalDate birthday;
}
