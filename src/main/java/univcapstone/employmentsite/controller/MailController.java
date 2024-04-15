package univcapstone.employmentsite.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import univcapstone.employmentsite.dto.MailDto;
import univcapstone.employmentsite.service.MailService;
import univcapstone.employmentsite.util.Constants;

import java.util.Map;

@Slf4j
@RestController
public class MailController {

    public final MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/confirm/email")
    public int confirmEmail(@RequestBody Map<String, String> receiverMap) {
        //이메일 인증에 대한 로직
        String email = receiverMap.get(Constants.EMAIL_KEY);

        int authNumber = MailService.createNumber();

        String content = generateAuthCodeFormat(authNumber);

        MailDto mailDto = MailDto.builder()
                .receiver(email)
                .subject("회원 가입 인증 번호입니다.")
                .content(content)
                .build();

        mailService.sendMail(mailDto);
        return authNumber;
    }

    private static String generateAuthCodeFormat(int authNumber) {

        return "<div style='margin:20px;'>" +
                "<h1> 안녕하세요 Job학다식입니다. </h1>" +
                "<br>" +
                "<p>아래 코드를 복사해 입력해주세요<p>" +
                "<br>" +
                "<p>감사합니다.<p>" +
                "<br>" +
                "<div align='center' style='border:1px solid black; font-family:verdana';>" +
                "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>" +
                "<div style='font-size:130%'>" +
                "CODE : <strong>" +
                authNumber +
                "</strong><div><br/>" +
                "</div>";
    }

}
