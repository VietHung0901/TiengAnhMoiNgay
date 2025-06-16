package Project.TiengAnhMoiNgay.services;

import Project.TiengAnhMoiNgay.constant.Provider;
import Project.TiengAnhMoiNgay.constant.Role;
import Project.TiengAnhMoiNgay.entities.User;
import Project.TiengAnhMoiNgay.model.StringURL;
import Project.TiengAnhMoiNgay.entities.VerificationToken;
import Project.TiengAnhMoiNgay.repositories.IRoleRepository;
import Project.TiengAnhMoiNgay.repositories.IUserRepository;
import Project.TiengAnhMoiNgay.repositories.VerificationTokenRepository;
import Project.TiengAnhMoiNgay.request.UserRegister;
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

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveOauthUser(@NotNull String email) {
        if(userRepository.findByUsername(email) != null)
            return;
        var user = new User();
        user.setUsername(email);
        user.setPassword(new BCryptPasswordEncoder().encode(email));
        user.setProvider(Provider.GOOGLE.value);
        user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
        userRepository.save(user);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
    public void Save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setProvider(Provider.LOCAL.value);
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

    public String createNewUser(UserRegister userRegister) {
        if(userRepository.existsByUsername(userRegister.getUsername())){
            return "Email này đã sử dụng cho tài khoản khác!";
        }
        if(userRepository.existsByPhone(userRegister.getPhone())){
            return "Số điện thoại này đã sử dụng cho tài khoản khác!";
        }
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

//        //Tạo token khi đăng ký
//        VerificationToken verificationToken = new VerificationToken();
//        verificationToken.setToken(UUID.randomUUID().toString());
//        verificationToken.setUser(user);
//        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1)); // Token hết hạn sau 1 ngày
//        verificationTokenRepository.save(verificationToken);
//
//        // Gửi email xác nhận
//        // Tạo URL xác nhận bằng cách sử dụng lớp StringURL
//        StringURL stringURL = new StringURL();  // Tạo đối tượng StringURL
//        String confirmationUrl = stringURL.getHttp() + "/confirm?token=" + verificationToken.getToken();  // Sử dụng getter để lấy http
//
//        emailService.sendEmail(user.getUsername(), "Xác nhận email", user, confirmationUrl);

        return "Success";
    }

}
