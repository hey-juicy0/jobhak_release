package univcapstone.employmentsite.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserEditDto {
    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
    @NotEmpty
    private String nickname;
    private String photo_id;
}
