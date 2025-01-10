package Project.TiengAnhMoiNgay.Repositories;

import Project.TiengAnhMoiNgay.Entities.User;
import Project.TiengAnhMoiNgay.Model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);

}