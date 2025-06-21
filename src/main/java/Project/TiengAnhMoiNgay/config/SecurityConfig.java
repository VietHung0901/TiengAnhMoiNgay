package Project.TiengAnhMoiNgay.config;

import Project.TiengAnhMoiNgay.services.OAuthService;
import Project.TiengAnhMoiNgay.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuthService oAuthService;
    private final UserService userService;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http.csrf().disable().sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Không dùng session
                ).authorizeHttpRequests(auth -> auth

                        // No access required
                        .requestMatchers("/js/**",
                                        "/assets/**",
                                        "/assets1/**",
                                        "/subtitles/**",
                                        "/api/auth/**",
                                        "/view/**",
                                        "/")
                                        .permitAll()

                        // access role: USER/EMPLOYEE/MANAGE
                        .requestMatchers("/api/listening_lesson/list/**",
                                        "/api/listening_lesson/detail/**",
                                        "/api/writing_lesson/list/**",
                                        "/api/writing_lesson/detail/**",
                                        "/writings/**")
                                        .hasAnyAuthority("USER", "EMPLOYEE", "MANAGER")

                        // access role: EMPLOYEE/MANAGE
                        .requestMatchers("/api/listening_lesson/create/**",
                                        "/api/writing_lesson/create")
                                        .hasAnyAuthority("EMPLOYEE", "MANAGER")

                        // authentication request
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).exceptionHandling(exceptionHandling -> exceptionHandling
                        // Xử lý lỗi 401 (Unauthorized)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"status\": \"error\", \"message\": \"Bạn cần đăng nhập để truy cập tài nguyên này\"}");
                        })
                        // Xử lý lỗi 403 (Forbidden)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"status\": \"error\", \"message\": \"Bạn không có quyền truy cập tài nguyên này\"}");
                        })).build();
    }
}