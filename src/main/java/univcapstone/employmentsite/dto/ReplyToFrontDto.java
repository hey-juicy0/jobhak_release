package univcapstone.employmentsite.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReplyToFrontDto {
    private Long replyId;
    private Long postId;
    private Long userId;
    private String nickname;
    private Long parentReplyId;
    private String replyContent;
    private String date;

    public ReplyToFrontDto(Long replyId, Long postId, Long userId, String nickname, Long parentReplyId, String replyContent, LocalDateTime date) {
        this.replyId = replyId;
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.parentReplyId = parentReplyId;
        this.replyContent = replyContent;
        this.date = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
