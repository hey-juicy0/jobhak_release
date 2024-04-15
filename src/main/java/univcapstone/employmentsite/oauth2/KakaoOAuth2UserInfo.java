package univcapstone.employmentsite.oauth2;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final String accessToken;
    private final String id; //회원번호
    private final String name;
    private final String email;
    private final String nickname;

    public KakaoOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
        this.accessToken = accessToken;
        this.attributes = attributes;

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        log.info("attributes = {}", attributes);

        this.id = getIdFromResponse(attributes);
        this.name = getNameFromResponse(kakaoAccount);
        this.email = getEmailFromResponse(kakaoAccount);
        this.nickname = getNicknameFromResponse(profile);
    }

    private String getIdFromResponse(Map<String, Object> attributes) {
        return ((Long) attributes.get("id")).toString();
    }

    private String getNameFromResponse(Map<String, Object> kakaoAccount) {
        return (String) kakaoAccount.get("name");
    }

    private String getEmailFromResponse(Map<String, Object> kakaoAccount) {
        return (String) kakaoAccount.get("email");
    }

    private String getNicknameFromResponse(Map<String, Object> profile) {
        return (String) profile.get("nickname");
    }

    @Override
    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getNickname() {
        return this.nickname;
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.KAKAO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}
