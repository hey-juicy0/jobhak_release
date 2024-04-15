package univcapstone.employmentsite.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import univcapstone.employmentsite.util.AuthConstants;

@Getter
@RedisHash(timeToLive = AuthConstants.OTP_TTL) //임시 비밀번호 30분 간 지속
public class Otp {

    @Id
    private Long id;
    @Indexed
    private String otp;

    @Builder
    public Otp(Long id, String otp) {
        this.id = id;
        this.otp = otp;
    }
}
