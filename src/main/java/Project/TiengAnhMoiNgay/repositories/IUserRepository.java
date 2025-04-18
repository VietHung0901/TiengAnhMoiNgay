package Project.TiengAnhMoiNgay.repositories;

import Project.TiengAnhMoiNgay.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);

    // Kiểm tra tồn tại
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);

    // Kiểm tra định dạng

}
