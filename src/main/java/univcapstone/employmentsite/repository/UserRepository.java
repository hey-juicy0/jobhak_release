package univcapstone.employmentsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import univcapstone.employmentsite.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByEmail(String email);

    boolean existsByLoginId(String loginId);
}
