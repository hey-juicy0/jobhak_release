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
public class PostDetailToFrontDto {
    private Long postId;
    private List<ReplyToFrontDto> replies = new ArrayList<>();
    private String category;
    private String title;
    private String content;
    private Map<String,String> fileName;
    private Long userId;
    private String nickname;
    private String date;

    private String writerProfile;
    private Map<String,String> replierProfile;

    private Map<String,String> WriterRealFileName;
    private List<Map<String,String>> ReplierRealFileName;
    public PostDetailToFrontDto(PostToFrontDto post) {
        this.postId = post.getPostId();
        this.replies = post.getReplies();
        this.category = post.getCategory();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userId = post.getUserId();
        this.nickname = post.getNickname();
        this.date = post.getDate();
        this.fileName=post.getFileName();
    }
}
