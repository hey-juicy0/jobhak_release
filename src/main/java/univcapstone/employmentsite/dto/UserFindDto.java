package univcapstone.employmentsite.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserFindDto {
    @Nullable
    private String name;
    @Nullable
    private String email;
    @Nullable
    private String loginId;
}
