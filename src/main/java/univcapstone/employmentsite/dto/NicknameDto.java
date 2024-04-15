package univcapstone.employmentsite.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter@Setter
public class NicknameDto {
    @NotEmpty
    private String nickname;
}
