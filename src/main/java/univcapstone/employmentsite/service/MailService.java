package univcapstone.employmentsite.service;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import univcapstone.employmentsite.dto.MailDto;

import java.util.Random;


@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendMail(MailDto mailDto) {

        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(mailDto.getReceiver()));
            mimeMessage.setFrom(new InternetAddress(sender));
            mimeMessage.setSubject(mailDto.getSubject(), "utf-8");
            mimeMessage.setContent(mailDto.getContent(), "text/html;charset=utf-8");
        };

        mailSender.send(preparator);
    }

    public static int createNumber() {
        Random random = new Random();
        return random.nextInt(90000) + 10000;
    }
}