package univcapstone.employmentsite.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = "post")
@EntityListeners(AuditingEntityListener.class)
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long replyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //대댓글의 부모 댓글 Id
    private Long parentReplyId;
    private String replyContent;

    @CreatedDate
    private LocalDateTime date;

    @Builder
    public Reply(Post post, User user, Long parentReplyId, String replyContent) {
        this.post = post;
        this.user = user;
        this.parentReplyId = parentReplyId;
        this.replyContent = replyContent;
    }

}
