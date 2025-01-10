package Project.TiengAnhMoiNgay.Services;

import Project.TiengAnhMoiNgay.Constant.Role;
import Project.TiengAnhMoiNgay.Entities.User;
import Project.TiengAnhMoiNgay.Model.StringURL;
import Project.TiengAnhMoiNgay.Model.VerificationToken;
import Project.TiengAnhMoiNgay.Repositories.IRoleRepository;
import Project.TiengAnhMoiNgay.Repositories.IUserRepository;
import Project.TiengAnhMoiNgay.Repositories.VerificationTokenRepository;
import Project.TiengAnhMoiNgay.Request.UserRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private EmailService emailService;

    @Transactional(isolation = Isolation.SERIALIZABLE,
            rollbackFor = {Exception.class, Throwable.class})
    public void Save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder()
                .encode(user.getPassword()));
        userRepository.save(user);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
    public void setDefaultRole(String username){
        userRepository.findByUsername(username)
                .getRoles()
                .add(roleRepository.findRoleById(Role.USER.value));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public User createNewUser(UserRegister userRegister) {
        User user = new User();
        user.setName(userRegister.getName());
        user.setUsername(userRegister.getUsername());
        user.setPhone(userRegister.getPhone());
        user.setPassword(userRegister.getPassword());
        user.setGender(userRegister.getGender());
        user.setBirthday(userRegister.getBirthday());
        user.setStatus(0);
        Save(user);
        setDefaultRole(user.getUsername());

        //Tạo token khi đăng ký
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1)); // Token hết hạn sau 1 ngày
        verificationTokenRepository.save(verificationToken);

        // Gửi email xác nhận
        // Tạo URL xác nhận bằng cách sử dụng lớp StringURL
        StringURL stringURL = new StringURL();  // Tạo đối tượng StringURL
        String confirmationUrl = stringURL.getHttp() + "/confirm?token=" + verificationToken.getToken();  // Sử dụng getter để lấy http

        emailService.sendEmail(user.getUsername(), "Xác nhận email", user, confirmationUrl);

        return user;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
