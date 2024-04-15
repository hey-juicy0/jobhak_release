package univcapstone.employmentsite.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter@Setter
public class UserDeleteDto {
    @NotEmpty
    private String loginId;
    
    @NotEmpty
    private String password;
}
