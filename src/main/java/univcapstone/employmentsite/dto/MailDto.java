package univcapstone.employmentsite.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailDto {

    private String receiver;
    private String subject;
    private String content;
}
