package univcapstone.employmentsite.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UserRequestDto {

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;

    private String nickname;

    private String email;

    private String name;
}
