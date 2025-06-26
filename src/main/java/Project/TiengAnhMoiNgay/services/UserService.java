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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
        user.getRoles()
                .add(roleRepository.findRoleById(Role.USER.value));
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

        return "Success";
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getNewUsersToday() {
        return userRepository.countByCreatedAt(LocalDate.now());
    }

    public long getNewUsersThisWeek() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
        return userRepository.countByCreatedAtBetween(startOfWeek, now);
    }

    public List<User> getLatestUsers() {
        return userRepository.findTop5ByOrderByCreatedAtDesc();
    }
}
