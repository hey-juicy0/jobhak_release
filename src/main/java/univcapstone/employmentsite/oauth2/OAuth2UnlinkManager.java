package univcapstone.employmentsite.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import univcapstone.employmentsite.oauth2.exception.OAuth2AuthenticationException;

@Component
@RequiredArgsConstructor
public class OAuth2UnlinkManager {

    private final KakaoOAuth2Unlink kakaoOAuth2Unlink;
    private final NaverOAuth2Unlink naverOAuth2Unlink;

    public void unlink(OAuth2Provider provider, String accessToken) {
        if (OAuth2Provider.NAVER.equals(provider)) {
            naverOAuth2Unlink.unlink(accessToken);
        } else if (OAuth2Provider.KAKAO.equals(provider)) {
            kakaoOAuth2Unlink.unlink(accessToken);
        } else {
            throw new OAuth2AuthenticationException(
                    "Unlink with " + provider.getProvider() + " is not supported");
        }
    }
}
