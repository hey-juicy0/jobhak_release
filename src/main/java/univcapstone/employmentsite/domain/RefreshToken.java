package univcapstone.employmentsite.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import univcapstone.employmentsite.util.AuthConstants;

@Getter
@RedisHash(value = "refreshToken", timeToLive = AuthConstants.REFRESH_TOKEN_VALID_TIME / 1000)
public class RefreshToken {

    @Id
    private String loginId;
    @Indexed
    private String refreshToken;

    @Builder
    public RefreshToken(String refreshToken, String loginId) {
        this.refreshToken = refreshToken;
        this.loginId = loginId;
    }
}
