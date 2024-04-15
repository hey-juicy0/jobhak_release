package univcapstone.employmentsite.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class NaverDto {

    @Value("{$spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;
    @Value("{$spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @NotEmpty
    private String accessToken;

    private String grantType;
}
