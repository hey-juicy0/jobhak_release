package univcapstone.employmentsite.oauth2;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> response;
    private final String accessToken;
    //회원의 네이버아이디는 출력결과에 포함되지 않는다. 대신 프로필 조회 api 호출 결과에 포함되는 'id'라는 값을 이용해서 회원을 구분
    private final String id; //네이버 아이디마다 고유하게 발급되는 유니크한 일련번호 값
    private final String name;
    private final String email;
    private final String nickname;

    public NaverOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
        this.accessToken = accessToken;
        //네이버에 기본 응답값(attributes)에는 resultcode와 message 밖에 없음. 따라서 response를 얻어와서 해당 필드에 있는 값들 사용
        this.response = (Map<String, Object>) attributes.get("response");

        this.id = getIdFromResponse(response);
        this.name = getNameFromResponse(response);
        this.email = getEmailFromResponse(response);
        this.nickname = getNicknameFromResponse(response);
    }

    private String getIdFromResponse(Map<String, Object> attributes) {
        return (String) attributes.get("id");
    }

    private String getNameFromResponse(Map<String, Object> attributes) {
        return (String) attributes.get("name");
    }

    private String getEmailFromResponse(Map<String, Object> attributes) {
        return (String) attributes.get("email");
    }

    private String getNicknameFromResponse(Map<String, Object> attributes) {
        return (String) attributes.get("nickname");
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
        return OAuth2Provider.NAVER;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.response;
    }

}
