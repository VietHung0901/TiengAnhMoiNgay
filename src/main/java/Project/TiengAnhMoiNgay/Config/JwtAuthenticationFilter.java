package Project.TiengAnhMoiNgay.Config;

import Project.TiengAnhMoiNgay.Model.JWTTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTTokenUtil jwtTokenUtil;

    public JwtAuthenticationFilter(JWTTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                // Kiểm tra token hết hạn
                if (jwtTokenUtil.isTokenExpired(token)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token đã hết hạn");
                    return; // Dừng chuỗi lọc
                }

                // Lấy username và vai trò từ token
                String username = jwtTokenUtil.extractUsername(token);
                List<String> roles = jwtTokenUtil.extractRoles(token);

                // Thêm vai trò vào SecurityContext
                if (username != null && roles != null) {
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                // Xử lý khi token hết hạn
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token đã hết hạn");
                return; // Dừng chuỗi lọc
            } catch (Exception e) {
                // Các lỗi khác (ví dụ: token không hợp lệ)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                return; // Dừng chuỗi lọc
            }
        }
        filterChain.doFilter(request, response);
    }
}
