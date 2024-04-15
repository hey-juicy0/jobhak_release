package univcapstone.employmentsite.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import univcapstone.employmentsite.domain.User;

@Data
public class UserResponseDto {

    private Long id;

    private String loginId;

    private String nickname;

    @Builder
    public UserResponseDto(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.nickname = user.getNickname();
    }
}
