package univcapstone.employmentsite.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import univcapstone.employmentsite.domain.Post;
import univcapstone.employmentsite.domain.User;

import java.time.LocalDateTime;

@Data
@Getter@Setter
@ToString
public class BookmarkToFront {
    private Long bookmarkId;
    private Long userId;
    private String loginId;
    private String name;
    private Long postId;
    private String title;
    private String category;
    private LocalDateTime date;

    public BookmarkToFront(Long bookmarkId, Long userId, String loginId, String name, Long postId, String title, String category, LocalDateTime date) {
        this.bookmarkId = bookmarkId;
        this.userId = userId;
        this.loginId = loginId;
        this.name = name;
        this.postId = postId;
        this.title = title;
        this.category = category;
        this.date = date;
    }
}
