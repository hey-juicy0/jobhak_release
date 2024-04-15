package univcapstone.employmentsite.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class KakaoOAuth2Unlink implements OAuth2Unlink {

    private static final String URL = "https://kapi.kakao.com/v1/user/unlink";
    private final RestTemplate restTemplate;

    @Override
    public void unlink(String accessToken) {

    }
}
