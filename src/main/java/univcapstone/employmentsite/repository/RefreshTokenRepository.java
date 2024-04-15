package univcapstone.employmentsite.repository;

import org.springframework.data.repository.CrudRepository;
import univcapstone.employmentsite.domain.RefreshToken;

import java.util.Optional;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findRefreshTokenByRefreshToken(String refreshToken);
}