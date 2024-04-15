package univcapstone.employmentsite.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReplyDto {

    @Nullable
    private Long parentReplyId; //대댓글의 부모 댓글 id

    @NotEmpty
    private String replyContent;

}
