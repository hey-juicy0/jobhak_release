package univcapstone.employmentsite.oauth2;

import univcapstone.employmentsite.oauth2.exception.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                   String accessToken,
                                                   Map<String, Object> attributes) {

        //네이버 로그인 시
        if (OAuth2Provider.NAVER.getProvider().equals(registrationId)) {
            return new NaverOAuth2UserInfo(accessToken, attributes);
        }
        //카카오 로그인 시
        else if (OAuth2Provider.KAKAO.getProvider().equals(registrationId)) {
            return new KakaoOAuth2UserInfo(accessToken, attributes);
        }
        throw new OAuth2AuthenticationException("Login with " + registrationId + " is not supported");
    }
}
