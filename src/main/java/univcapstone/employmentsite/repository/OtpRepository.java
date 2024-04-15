package univcapstone.employmentsite.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import univcapstone.employmentsite.domain.Otp;

//임시 비밀번호 저장소 (Redis 사용)
public interface OtpRepository extends CrudRepository<Otp, Long> { //key: 사용자 ID, value: OTP

}
