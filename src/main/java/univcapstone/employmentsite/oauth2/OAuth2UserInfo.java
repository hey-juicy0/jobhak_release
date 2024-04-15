package univcapstone.employmentsite.oauth2;

import java.util.Map;

public interface OAuth2UserInfo {

    String getAccessToken();

    String getId();

    String getName();

    String getEmail();

    String getNickname();

    OAuth2Provider getProvider();

    Map<String, Object> getAttributes();







}
