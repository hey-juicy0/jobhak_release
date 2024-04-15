package univcapstone.employmentsite.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jdk.jfr.Category;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import univcapstone.employmentsite.domain.Post;
import univcapstone.employmentsite.domain.User;

import java.util.List;

@Data
@Getter
@Setter
public class PostDto {

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @Nullable
    private String category;

    public Post toEntity(User user, PostDto postDto) {
        return Post.builder()
                .user(user)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .category(postDto.getCategory())
                .build();
    }

    public Post toEntity(PostDto postDto) {
        return Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .build();
    }
}
