package univcapstone.employmentsite.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import univcapstone.employmentsite.dto.PostToFrontDto;
import univcapstone.employmentsite.dto.ReplyToFrontDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Reply> replies = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmark = new ArrayList<>();

    private String category;
    private String title;
    private String content;

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostFile> postFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    private LocalDateTime date;

    @Builder
    public Post(User user, String title, String content, String category) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.category = category;
    }

    @Builder
    public Post(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static PostToFrontDto convertPostDTO(Post post) {
        List<ReplyToFrontDto> replyToFront = new ArrayList<>();

        for (Reply reply : post.getReplies()) {
            replyToFront.add(new ReplyToFrontDto(reply.getReplyId(),
                    reply.getPost().getPostId(),
                    reply.getUser().getId(),
                    reply.getUser().getNickname(),
                    reply.getParentReplyId(),
                    reply.getReplyContent(),
                    reply.getDate()));
        }

        return new PostToFrontDto(post.getPostId(),
                replyToFront,
                post.getCategory(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getDate()
        );
    }
}
