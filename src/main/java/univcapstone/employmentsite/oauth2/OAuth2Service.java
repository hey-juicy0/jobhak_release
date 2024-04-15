package univcapstone.employmentsite.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

    //스프링 시큐리티 OAuth2LoginAuthenticationFilter 에서 시작된 OAuth2 인증 과정 중에 호출되는 메서드
    //액세스 토큰을 OAuth2 Provider로부터 받았을 때 호출
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = delegate.loadUser(oAuth2UserRequest);

        // OAuth2UserRequest.getClientRegistration().getRegistrationId()에 oauth2 제공처(네이버 or 카카오) 값이 들어있다.
        // {registrationId='naver'} 이런식으로
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        String accessToken = oAuth2UserRequest.getAccessToken().getTokenValue();

        OAuth2UserInfo oAuth2UserInfo =
                OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, accessToken, oAuth2User.getAttributes());

        return new CustomOAuth2User(oAuth2UserInfo);
    }
}
