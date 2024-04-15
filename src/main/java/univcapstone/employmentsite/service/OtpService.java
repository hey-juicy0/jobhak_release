package univcapstone.employmentsite.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.Otp;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.repository.OtpRepository;
import univcapstone.employmentsite.repository.UserRepository;

import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final String chars =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "0123456789" +
                    "~`!@#$%^&*()-_=+\\|[{]};:'\",<.>/?";

    //임시 비밀번호를 사용자의 비밀번호로 변경하는 방식
    public String saveOtp(Long userId, int length) {
        String randomPassword = generateOtp(length);

        log.info("생성한 랜덤 패스워드 = {}", randomPassword);

        String encodedOtp = userService.updatePassword(userId, randomPassword);

        log.info("암호화한 비밀번호 = {}, 복호화한 비밀번호 isEqual? = {}", encodedOtp,
                passwordEncoder.matches(randomPassword, encodedOtp));

        Otp otp = Otp.builder()
                .id(userId)
                .otp(encodedOtp)
                .build();

        otpRepository.save(otp);

        return randomPassword;
    }

    private String generateOtp(int length) {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(new Date().getTime());
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; i++) {
            int randomInt = secureRandom.nextInt(chars.length());
            sb.append(chars.charAt(randomInt));
        }

        return sb.toString();
    }

}
