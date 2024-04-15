package univcapstone.employmentsite.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PostToFrontDto {
    private Long postId;
    private List<ReplyToFrontDto> replies = new ArrayList<>();
    private String category;
    private String title;
    private String content;
    private Map<String,String> fileName;
    private Long userId;
    private String nickname;
    private String date;

    public PostToFrontDto(Long postId, List<ReplyToFrontDto> replies, String category, String title, String content,Long userId, String nickname, LocalDateTime date) {
        this.postId = postId;
        this.replies = replies;
        this.category = category;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.nickname = nickname;
        this.date = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
