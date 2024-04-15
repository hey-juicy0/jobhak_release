package univcapstone.employmentsite.dto;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReplyUpdateDto {
    private Long replyId;
    private String replyContent;
}
