package univcapstone.employmentsite.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class NaverLoginDto {
    @NotEmpty
    private String loginId;

    private String name;

    private String nickname;

    private String email;
}
