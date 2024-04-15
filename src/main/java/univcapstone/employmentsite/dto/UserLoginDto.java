package univcapstone.employmentsite.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import univcapstone.employmentsite.domain.User;

@Data
public class UserLoginDto {

    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;

//    public UsernamePasswordAuthenticationToken toAuthenticationToken() {
//        return new UsernamePasswordAuthenticationToken()
//    }

}
