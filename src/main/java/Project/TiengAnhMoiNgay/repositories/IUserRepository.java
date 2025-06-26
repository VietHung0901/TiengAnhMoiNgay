package Project.TiengAnhMoiNgay.repositories;

import Project.TiengAnhMoiNgay.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);

    // Kiểm tra tồn tại
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);

    // Tổng số người dùng
    long count();

    // Số người dùng tạo hôm nay
    long countByCreatedAt(LocalDate date);

    // Số người dùng tạo trong khoảng thời gian
    long countByCreatedAtBetween(LocalDate start, LocalDate end);

    // Optional: Lấy danh sách người dùng mới nhất
    List<User> findTop5ByOrderByCreatedAtDesc();
}
